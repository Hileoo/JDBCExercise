import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * This class is define the database which would be used and execute the backup
 */
public class Main {
	Backup myDbUser = null;
	private Scanner input;

	private void go() throws SQLException, IOException
	{	
		System.out.println("Please input the database name which would be backup: ");
		input = new Scanner(System.in);
		String dbName = input.nextLine();
		
		System.out.println("In go...");
		myDbUser = new Backup(dbName + ".db");	
		myDbUser.createOutput(dbName);
		
		System.out.println("Processing over");

		myDbUser.close();
	}; // end of method "go"

	public static void main(String [ ] args) throws SQLException, IOException
	{
		Main myMain = new Main();
		myMain.go();
	} // end of method "main"

} // end of class "Main"
