import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class used to provide the backup function for the specific SQLite database
 */
public class Backup extends DbBasic {
	private DatabaseMetaData data;
	private ArrayList<String> columnNames = new ArrayList<String>();
	private ArrayList<String> columnTypeNames = new ArrayList<String>();
	private ArrayList<String> columnNull = new ArrayList<String>();
	private ArrayList<String> primaryKeyNames = new ArrayList<String>();
	private ArrayList<String> referPKTableNames = new ArrayList<String>();
	private ArrayList<String> referPKColumnNames = new ArrayList<String>();
	private ArrayList<String> foreignKeyNames = new ArrayList<String>();
	private ArrayList<String> createIndex = new ArrayList<String>();

	/** Create a connection to the named database **/
	Backup(String dbName) throws SQLException {
		super( dbName );
		data = con.getMetaData();
	}
	
	/** Output: Display DbInfo, Drop exist file, CREATE TABLE + INDEX, INSERT TABLE, BATCH FILE **/
	public void createOutput(String dbFile) throws SQLException, IOException {	
		displayDatabaseInfo();
		prepareFile(dbFile);
		/* Operate for each table */
		ArrayList<String> tableNames = getSortedTableNames();
		/* Write CREATE and access INDEX */
		for(String thisTableName : tableNames) {
			createTable(dbFile, thisTableName);
			getIndex(thisTableName);		
		}
		
		/* Write index line by line */
		if(createIndex.size() > 0) 
			createIndex.set(createIndex.size() - 1, createIndex.get(createIndex.size() - 1) + ");");
		for(String item: createIndex) 
			writeFile("create_table_" + dbFile + ".sql", item);
		createIndex.clear();
		
		/* Write batch file */
		String createCommand = "sqlite3 " + dbFile + "_Backup.db<create_table_" + dbFile + ".sql \n";
		String insertCommand = "sqlite3 " + dbFile + "_Backup.db<insert_table_" + dbFile + ".sql \n";
		writeFile(dbFile + "_Backup.sh", createCommand + insertCommand);
	}
	
	/** Do Create Table statement and write to file**/
	public void createTable(String db, String tableName) throws SQLException, IOException {
		getColumnNames(tableName);
		getKeys(tableName);
		String createStatement = "CREATE TABLE \"" + tableName + "\" (\n ";
		
		/* Create columns */
		for(int i = 0; i < columnNames.size(); i++) {
			if (i == 0)  //For first column
				createStatement = createStatement + columnNames.get(0) + " " + columnTypeNames.get(0).toLowerCase();
			else  //For other columns
				createStatement = createStatement + ",\n " + columnNames.get(i) + " " + columnTypeNames.get(i).toLowerCase();
			if (columnNull.get(i).equals("0"))  //To check "Not Null"
				createStatement = createStatement + " not null ";
		}
		
		/* Create primary keys */
		for(int i = 0; i < primaryKeyNames.size(); i++) {
			if(primaryKeyNames.size() == 1)  //For only one primary key
				createStatement = createStatement + ",\n primary key (" + primaryKeyNames.get(i) + ")";
			else if((primaryKeyNames.size() != 1) && ( i==0 ))  //For various primary key: 1st primary key
				createStatement = createStatement + ",\n primary key (" + primaryKeyNames.get(i); 
			else if(i == primaryKeyNames.size() - 1)  //For various primary key: last primary key
				createStatement = createStatement + ", "+ primaryKeyNames.get(i) + ")";
			else
				createStatement = createStatement + ", "+ primaryKeyNames.get(i);
		}
		
		/* Create foreign keys */
		if(foreignKeyNames.size() != 0) {
			for(int i = foreignKeyNames.size() - 1; i >= 0; i--) {
				if(i == 0)  //For 1st foreign key
					createStatement = createStatement + ",\n foreign key ("+ foreignKeyNames.get(i)+") references \"" + referPKTableNames.get(i) + "\"(" + referPKColumnNames.get(i) + ")";
				else  //For other foreign keys
					createStatement = createStatement + ",\n foreign key ("+ foreignKeyNames.get(i)+") references \"" + referPKTableNames.get(i) + "\"(" + referPKColumnNames.get(i) + ")";
			}
		}
		
		/* Do the end line */
		createStatement = createStatement + "\n );\n";
		
		/* Write to the file */
		writeFile("create_table_" + db + ".sql", createStatement);
	
		/* Clear the data for this table */
		createStatement = "";
		columnNames.clear();
		columnTypeNames.clear();
		columnNull.clear();
		primaryKeyNames.clear();
		referPKTableNames.clear();
		referPKColumnNames.clear();
		foreignKeyNames.clear();
		
		/* Call the insert function */
		insertOp(db, tableName);
	}
	
	/** Get the primary key, also the table name, column name and foreignKey name of reference table**/
	public void getKeys(String table) throws SQLException {
		/* Get primary key */
		ResultSet keyRS = data.getPrimaryKeys(null, null, table);
		while (keyRS.next()) 
			primaryKeyNames.add(keyRS.getString("COLUMN_NAME"));
		/* Get the information of reference table */
		ResultSet foreignKeysRS = data.getImportedKeys(null, null, table);
		while (foreignKeysRS.next()) {
			referPKTableNames.add(foreignKeysRS.getString("PKTABLE_NAME"));
			referPKColumnNames.add(foreignKeysRS.getString("PKCOLUMN_NAME"));
			foreignKeyNames.add(foreignKeysRS.getString("FKCOLUMN_NAME"));
		}
	}
	
	/** Get name, type and NOT NULL status of column **/
	public void getColumnNames(String table) throws SQLException {
		ResultSet columnRS = data.getColumns(null, null, table, null);
		while (columnRS.next()) {
			columnNames.add(columnRS.getString("COLUMN_NAME"));
			columnTypeNames.add(columnRS.getString("TYPE_NAME"));
			columnNull.add(columnRS.getString("NULLABLE"));
		}
	}
	
	/** Get the table names and sorted them, used for CREATE TABLE by order **/
	public ArrayList<String> getSortedTableNames() throws SQLException {
		ArrayList<String> sortedTableNames = new ArrayList<String>();
		ResultSet tableRS = data.getTables(null, null, null, new String[] { "TABLE", "VIEW" });
		/* Do for each table */
		while (tableRS.next()) {
			ResultSet fks = data.getImportedKeys(null, null, tableRS.getString("TABLE_NAME"));
			/* Do for each foreign key */
			while (fks.next()) {
				/* Get the index/location of this table in sorted table names */
				int index = sortedTableNames.indexOf(tableRS.getString("TABLE_NAME"));
				String referTableName = fks.getString("PKTABLE_NAME");
				
				/* TableNames is null (first time) */
				if(index == -1) {
					if(!sortedTableNames.contains(referTableName)) 
						sortedTableNames.add(referTableName);  // If has refer table, add refer firstly
					if(!sortedTableNames.contains(tableRS.getString("TABLE_NAME")))
						sortedTableNames.add(tableRS.getString("TABLE_NAME"));  // Then add this table
				}
				/* TableNames not null (rest time) */
				else {
					if(!sortedTableNames.contains(referTableName))
						sortedTableNames.add(index, referTableName);  // If has refer table, add refer first
					else {
						if(sortedTableNames.indexOf(referTableName) > index) {
							sortedTableNames.remove(referTableName);
							sortedTableNames.add(index, referTableName);  // If refer table order behind this table, put it to the front of this table
						}
					}
				}
			}
			/* After checking each foreign key, put this table name to sortedTableNames if not exist */
			if(!sortedTableNames.contains(tableRS.getString("TABLE_NAME")))
				sortedTableNames.add(tableRS.getString("TABLE_NAME"));
		}
		return sortedTableNames;
	}
	
	/** Get the INDEX statement from Table **/
	public void getIndex(String table) throws SQLException {
		ResultSet indexInfoRS = data.getIndexInfo(null, null, table, false, true);
		String index = "";
		
		while(indexInfoRS.next()) {
			/* Skip the "auto-index" */ 
			if(indexInfoRS.getString("INDEX_NAME").contains("sqlite_autoindex_"))
				continue;
			/* When the column number of index equals 1 [e.g.: ON 1 table (1 column) ]*/
			if(indexInfoRS.getString("ORDINAL_POSITION").equals("1")) {
				/* Add ");\n" to the complete previous INDEX statement, modify createIndex list with correct index and content */
				if(!(createIndex.size() == 0))
					createIndex.set(createIndex.size() - 1, createIndex.get(createIndex.size() - 1) + ");\n");
				
				/* Create the CREATE INDEX statement */
				if(indexInfoRS.getString("NON_UNIQUE").equals("1")) 
					index = "CREATE INDEX [" + indexInfoRS.getString("INDEX_NAME") + "] ON [" +indexInfoRS.getString("TABLE_NAME") + "] ([" + indexInfoRS.getString("COLUMN_NAME");
				else 
					index = "CREATE UNIQUE INDEX [" + indexInfoRS.getString("INDEX_NAME") + "] ON [" + indexInfoRS.getString("TABLE_NAME") + "] ([" + indexInfoRS.getString("COLUMN_NAME");
				
				/* check ASC/DESC */
				if(indexInfoRS.getString("ASC_OR_DESC") == null)
					index = index + "]";
				else if(indexInfoRS.getString("ASC_OR_DESC").equals("D"))
					index = index + " DESC]";
				else if(indexInfoRS.getString("ASC_OR_DESC").equals("A"))
					index = index + " ASC]";
				
				/* Add this INDEX statement to createIndex list */
				createIndex.add(index);
			}
			/* When the column number of index not 1 [e.g.: ON 1 table (2 columns) ] */
			else {
				if(indexInfoRS.getString("ASC_OR_DESC") == null)
					createIndex.set(createIndex.size() - 1, createIndex.get(createIndex.size() - 1) + ", [" + indexInfoRS.getString("COLUMN_NAME") + "]");
				else if(indexInfoRS.getString("ASC_OR_DESC").equals("A"))
					createIndex.set(createIndex.size() - 1, createIndex.get(createIndex.size() - 1) + ", [" + indexInfoRS.getString("COLUMN_NAME") + " ASC]");
				else if(indexInfoRS.getString("ASC_OR_DESC").equals("D"))
					createIndex.set(createIndex.size() - 1, createIndex.get(createIndex.size() - 1) + ", [" + indexInfoRS.getString("COLUMN_NAME") + " DESC]");
			}
		}
	}
	
	/** Get the INSERT statement **/
	public void insertOp(String db, String table) throws SQLException, IOException {
		/* Execute select query */
		String select = "SELECT * FROM \"" + table + "\" ;";
		PreparedStatement ps = con.prepareStatement(select);
		ResultSet queryRS = ps.executeQuery();
		
		/* Do insert operation */
		String insert = "INSERT INTO \"" + table + "\" VALUES (";
		ResultSetMetaData dataRS = ps.getMetaData();		
		int columnCount = dataRS.getColumnCount();
		
		while(queryRS.next()) {
			String insert1 = insert;  // Without quotes
			String insert2 = insert;  // With quotes
			/* Get result (tuple) for each column */
			for(int i = 1; i <= columnCount; i++) {
				String result = queryRS.getString(i);
				/* Replace ' in tuple to avoid mistake */
				if(result != null)
					result = result.replaceAll("\"", "\'");
				/* Add ");\n" to the end of each INSERT statement */
				if(i == columnCount) {
					insert1 = insert1 + result + ");\n";
					insert2 = insert2 + "\"" + result + "\");\n";
				}
				/* Separate each value element with "," */
				else {
					insert1 = insert1 + result + ", ";
					insert2 = insert2 + "\"" + result +"\", ";
				}
			}
			/* Continuously write each INSERT statement to file*/
			writeFile("insert_table_" + db + "_a.sql", insert1);
			writeFile("insert_table_" + db + ".sql", insert2);
		}
	}
	
	/** Recreate all the related output files for this database **/
	public void prepareFile(String dbName) {
		checkFileExist("create_table_" + dbName + ".sql");
		checkFileExist("insert_table_" + dbName + ".sql");
		checkFileExist("insert_table_" + dbName + "_a.sql");
		checkFileExist(dbName + "_Backup.db");
		checkFileExist(dbName + "_Backup.sh");
	}
	
	/** Delete/Drop the file if it exist **/
	public void checkFileExist(String fileName) {
		File f = new File(fileName);
		if(f.exists()) {
			f.delete();
			System.out.println("Drop exist file: " + fileName);
		}
	}
	
	/** Show the information of this Database **/
	public void displayDatabaseInfo() throws SQLException {
		System.out.println("------------------Database Info--------------------");
		System.out.println("Database Product Name: " + data.getDatabaseProductName());
		System.out.println("Database Product Version: " + data.getDatabaseProductVersion());
		System.out.println("Database Driver Version: " + data.getDriverVersion());
		System.out.println("-----------------------END-------------------------");	
	}
	
	/** Write specific data to specific file **/
	public void writeFile(String file, String data) throws IOException {
		File f =new File(file);
		if(!f.exists())
			f.createNewFile();
		
		FileWriter fileWritter = new FileWriter(file, true);
		fileWritter.write(data);
		fileWritter.close();
	}
}
