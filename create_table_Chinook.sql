CREATE TABLE "Artist" (
 ArtistId integer not null ,
 Name nvarchar(120),
 primary key (ArtistId)
 );
CREATE TABLE "Album" (
 AlbumId integer not null ,
 Title nvarchar(160) not null ,
 ArtistId integer not null ,
 primary key (AlbumId),
 foreign key (ArtistId) references "Artist"(ArtistId)
 );
CREATE TABLE "Employee" (
 EmployeeId integer not null ,
 LastName nvarchar(20) not null ,
 FirstName nvarchar(20) not null ,
 Title nvarchar(30),
 ReportsTo integer,
 BirthDate datetime,
 HireDate datetime,
 Address nvarchar(70),
 City nvarchar(40),
 State nvarchar(40),
 Country nvarchar(40),
 PostalCode nvarchar(10),
 Phone nvarchar(24),
 Fax nvarchar(24),
 Email nvarchar(60),
 primary key (EmployeeId),
 foreign key (ReportsTo) references "Employee"(EmployeeId)
 );
CREATE TABLE "Customer" (
 CustomerId integer not null ,
 FirstName nvarchar(40) not null ,
 LastName nvarchar(20) not null ,
 Company nvarchar(80),
 Address nvarchar(70),
 City nvarchar(40),
 State nvarchar(40),
 Country nvarchar(40),
 PostalCode nvarchar(10),
 Phone nvarchar(24),
 Fax nvarchar(24),
 Email nvarchar(60) not null ,
 SupportRepId integer,
 primary key (CustomerId),
 foreign key (SupportRepId) references "Employee"(EmployeeId)
 );
CREATE TABLE "Genre" (
 GenreId integer not null ,
 Name nvarchar(120),
 primary key (GenreId)
 );
CREATE TABLE "Invoice" (
 InvoiceId integer not null ,
 CustomerId integer not null ,
 InvoiceDate datetime not null ,
 BillingAddress nvarchar(70),
 BillingCity nvarchar(40),
 BillingState nvarchar(40),
 BillingCountry nvarchar(40),
 BillingPostalCode nvarchar(10),
 Total numeric(10,2) not null ,
 primary key (InvoiceId),
 foreign key (CustomerId) references "Customer"(CustomerId)
 );
CREATE TABLE "MediaType" (
 MediaTypeId integer not null ,
 Name nvarchar(120),
 primary key (MediaTypeId)
 );
CREATE TABLE "Track" (
 TrackId integer not null ,
 Name nvarchar(200) not null ,
 AlbumId integer,
 MediaTypeId integer not null ,
 GenreId integer,
 Composer nvarchar(220),
 Milliseconds integer not null ,
 Bytes integer,
 UnitPrice numeric(10,2) not null ,
 primary key (TrackId),
 foreign key (AlbumId) references "Album"(AlbumId),
 foreign key (GenreId) references "Genre"(GenreId),
 foreign key (MediaTypeId) references "MediaType"(MediaTypeId)
 );
CREATE TABLE "InvoiceLine" (
 InvoiceLineId integer not null ,
 InvoiceId integer not null ,
 TrackId integer not null ,
 UnitPrice numeric(10,2) not null ,
 Quantity integer not null ,
 primary key (InvoiceLineId),
 foreign key (InvoiceId) references "Invoice"(InvoiceId),
 foreign key (TrackId) references "Track"(TrackId)
 );
CREATE TABLE "Playlist" (
 PlaylistId integer not null ,
 Name nvarchar(120),
 primary key (PlaylistId)
 );
CREATE TABLE "PlaylistTrack" (
 PlaylistId integer not null ,
 TrackId integer not null ,
 primary key (PlaylistId, TrackId),
 foreign key (PlaylistId) references "Playlist"(PlaylistId),
 foreign key (TrackId) references "Track"(TrackId)
 );
CREATE UNIQUE INDEX [IPK_Artist] ON [Artist] ([ArtistId]);
CREATE INDEX [IFK_AlbumArtistId] ON [Album] ([ArtistId]);
CREATE UNIQUE INDEX [IPK_Album] ON [Album] ([AlbumId]);
CREATE INDEX [IFK_EmployeeReportsTo] ON [Employee] ([ReportsTo]);
CREATE UNIQUE INDEX [IPK_Employee] ON [Employee] ([EmployeeId]);
CREATE INDEX [IFK_CustomerSupportRepId] ON [Customer] ([SupportRepId]);
CREATE UNIQUE INDEX [IPK_Customer] ON [Customer] ([CustomerId]);
CREATE UNIQUE INDEX [IPK_Genre] ON [Genre] ([GenreId]);
CREATE INDEX [IFK_InvoiceCustomerId] ON [Invoice] ([CustomerId]);
CREATE UNIQUE INDEX [IPK_Invoice] ON [Invoice] ([InvoiceId]);
CREATE UNIQUE INDEX [IPK_MediaType] ON [MediaType] ([MediaTypeId]);
CREATE INDEX [IFK_TrackMediaTypeId] ON [Track] ([MediaTypeId]);
CREATE INDEX [IFK_TrackGenreId] ON [Track] ([GenreId]);
CREATE INDEX [IFK_TrackAlbumId] ON [Track] ([AlbumId]);
CREATE UNIQUE INDEX [IPK_Track] ON [Track] ([TrackId]);
CREATE INDEX [IFK_InvoiceLineTrackId] ON [InvoiceLine] ([TrackId]);
CREATE INDEX [IFK_InvoiceLineInvoiceId] ON [InvoiceLine] ([InvoiceId]);
CREATE UNIQUE INDEX [IPK_InvoiceLine] ON [InvoiceLine] ([InvoiceLineId]);
CREATE UNIQUE INDEX [IPK_Playlist] ON [Playlist] ([PlaylistId]);
CREATE INDEX [IFK_PlaylistTrackTrackId] ON [PlaylistTrack] ([TrackId]);
CREATE UNIQUE INDEX [IPK_PlaylistTrack] ON [PlaylistTrack] ([PlaylistId], [TrackId]);