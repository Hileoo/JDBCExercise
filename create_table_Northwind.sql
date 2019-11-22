CREATE TABLE "Categories" (
 CategoryID int not null ,
 CategoryName varchar(15) not null ,
 Description text,
 Picture blob,
 primary key (CategoryID)
 );
CREATE TABLE "CustomerCustomerDemo" (
 CustomerID varchar(5) not null ,
 CustomerTypeID varchar(10) not null ,
 primary key (CustomerID, CustomerTypeID)
 );
CREATE TABLE "CustomerDemographics" (
 CustomerTypeID varchar(10) not null ,
 CustomerDesc text,
 primary key (CustomerTypeID)
 );
CREATE TABLE "Customers" (
 CustomerID varchar(5) not null ,
 CompanyName varchar(40) not null ,
 ContactName varchar(30),
 ContactTitle varchar(30),
 Address varchar(60),
 City varchar(15),
 Region varchar(15),
 PostalCode varchar(10),
 Country varchar(15),
 Phone varchar(24),
 Fax varchar(24),
 primary key (CustomerID)
 );
CREATE TABLE "Employees" (
 EmployeeID int not null ,
 LastName varchar(20) not null ,
 FirstName varchar(10) not null ,
 Title varchar(30),
 TitleOfCourtesy varchar(25),
 BirthDate timestamp,
 HireDate timestamp,
 Address varchar(60),
 City varchar(15),
 Region varchar(15),
 PostalCode varchar(10),
 Country varchar(15),
 HomePhone varchar(24),
 Extension varchar(4),
 Photo blob,
 Notes text,
 ReportsTo int,
 PhotoPath varchar(255),
 primary key (EmployeeID)
 );
CREATE TABLE "EmployeeTerritories" (
 EmployeeID int not null ,
 TerritoryID varchar(20) not null ,
 primary key (EmployeeID, TerritoryID)
 );
CREATE TABLE "Order Details" (
 OrderID int,
 ProductID int,
 UnitPrice float(26),
 Quantity int,
 Discount float(13),
 primary key (OrderID, ProductID)
 );
CREATE TABLE "Orders" (
 OrderID int not null ,
 CustomerID varchar(5),
 EmployeeID int,
 OrderDate timestamp,
 RequiredDate timestamp,
 ShippedDate timestamp,
 ShipVia int,
 Freight float(26),
 ShipName varchar(40),
 ShipAddress varchar(60),
 ShipCity varchar(15),
 ShipRegion varchar(15),
 ShipPostalCode varchar(10),
 ShipCountry varchar(15),
 primary key (OrderID)
 );
CREATE TABLE "Products" (
 ProductID int not null ,
 ProductName varchar(40) not null ,
 SupplierID int,
 CategoryID int,
 QuantityPerUnit varchar(20),
 UnitPrice float(26),
 UnitsInStock int,
 UnitsOnOrder int,
 ReorderLevel int,
 Discontinued int not null ,
 primary key (ProductID)
 );
CREATE TABLE "Region" (
 RegionID int not null ,
 RegionDescription varchar(50) not null ,
 primary key (RegionID)
 );
CREATE TABLE "Shippers" (
 ShipperID int not null ,
 CompanyName varchar(40) not null ,
 Phone varchar(24),
 primary key (ShipperID)
 );
CREATE TABLE "Suppliers" (
 SupplierID int not null ,
 CompanyName varchar(40) not null ,
 ContactName varchar(30),
 ContactTitle varchar(30),
 Address varchar(60),
 City varchar(15),
 Region varchar(15),
 PostalCode varchar(10),
 Country varchar(15),
 Phone varchar(24),
 Fax varchar(24),
 HomePage text,
 primary key (SupplierID)
 );
CREATE TABLE "Territories" (
 TerritoryID varchar(20) not null ,
 TerritoryDescription varchar(50) not null ,
 RegionID int not null ,
 primary key (TerritoryID)
 );
CREATE TABLE "Alphabetical list of products" (
 ProductID int,
 ProductName varchar(40),
 SupplierID int,
 CategoryID int,
 QuantityPerUnit varchar(20),
 UnitPrice float(26),
 UnitsInStock int,
 UnitsOnOrder int,
 ReorderLevel int,
 Discontinued int,
 CategoryName varchar(15)
 );
CREATE TABLE "Current Product List" (
 ProductID int,
 ProductName varchar(40)
 );
CREATE TABLE "Customer and Suppliers by City" (
 City varchar(15),
 CompanyName varchar(40),
 ContactName varchar(30),
 Relationship 
 );
CREATE TABLE "Order Details Extended" (
 OrderID int,
 ProductID int,
 ProductName varchar(40),
 UnitPrice float(26),
 Quantity int,
 Discount float(13),
 ExtendedPrice 
 );
CREATE TABLE "Order Subtotals" (
 OrderID int,
 Subtotal 
 );
CREATE TABLE "Summary of Sales by Quarter" (
 ShippedDate timestamp,
 OrderID int,
 Subtotal 
 );
CREATE TABLE "Summary of Sales by Year" (
 ShippedDate timestamp,
 OrderID int,
 Subtotal 
 );
CREATE TABLE "Orders Qry" (
 OrderID int,
 CustomerID varchar(5),
 EmployeeID int,
 OrderDate timestamp,
 RequiredDate timestamp,
 ShippedDate timestamp,
 ShipVia int,
 Freight float(26),
 ShipName varchar(40),
 ShipAddress varchar(60),
 ShipCity varchar(15),
 ShipRegion varchar(15),
 ShipPostalCode varchar(10),
 ShipCountry varchar(15),
 CompanyName varchar(40),
 Address varchar(60),
 City varchar(15),
 Region varchar(15),
 PostalCode varchar(10),
 Country varchar(15)
 );
CREATE TABLE "Products Above Average Price" (
 ProductName varchar(40),
 UnitPrice float(26)
 );
CREATE TABLE "Products by Category" (
 CategoryName varchar(15),
 ProductName varchar(40),
 QuantityPerUnit varchar(20),
 UnitsInStock int,
 Discontinued int
 );
