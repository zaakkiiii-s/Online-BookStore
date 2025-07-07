
-- Supertype: Customer
CREATE TABLE Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    NameSurname VARCHAR(100),
    Email VARCHAR(100) DEFAULT 'noemail@domain.com',
    PhoneNumber VARCHAR(10) DEFAULT '0000000000',
    Address TEXT,
    RegistrationDate DATE DEFAULT (CURRENT_DATE),
    CustomerType ENUM('Regular', 'Premium'),
    CHECK (CHAR_LENGTH(PhoneNumber) = 10),
    CHECK (CustomerType IN ('Regular', 'Premium'))
);

-- Subtype: RegularCustomer
CREATE TABLE RegularCustomer (
    CustomerID INT PRIMARY KEY,
    LoyaltyPoints INT DEFAULT 0,
    CHECK (LoyaltyPoints >= 0),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- Subtype: PremiumCustomer
CREATE TABLE PremiumCustomer (
    CustomerID INT PRIMARY KEY,
    SubscriptionStartDate DATE,
    SubscriptionEndDate DATE,
    DiscountRate DECIMAL(5,2) DEFAULT 10.00,
    CHECK (DiscountRate >= 0 AND DiscountRate <= 100),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- Customer Preferences
CREATE TABLE CustomerPreferences (
    PreferenceID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT,
    PreferenceScore INT DEFAULT 0,
    CHECK (PreferenceScore BETWEEN 0 AND 10),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- Cart
CREATE TABLE Cart (
    CartID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT,
    CreatedDate DATE DEFAULT (CURRENT_DATE),
    TotalPrice DECIMAL(10,2) DEFAULT 0.00,
    CHECK (TotalPrice >= 0),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- Supertype: Book
CREATE TABLE Book (
    BookID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255),
    Price DECIMAL(10,2) DEFAULT 0.00,
    StockQuantity INT DEFAULT 0,
    PublicationYear YEAR DEFAULT 2020,
    BookType ENUM('Physical', 'E-Book') DEFAULT 'Physical',
    CHECK (Price >= 0),
    CHECK (StockQuantity >= 0)
);

-- Subtype: PhysicalBook
CREATE TABLE PhysicalBook (
    BookID INT PRIMARY KEY,
    Weight DECIMAL(6,2) DEFAULT 0.00,
    Dimensions VARCHAR(100) DEFAULT 'N/A',
    FOREIGN KEY (BookID) REFERENCES Book(BookID)
);

-- Subtype: EBook
CREATE TABLE EBook (
    BookID INT PRIMARY KEY,
    FileFormat VARCHAR(50) DEFAULT 'PDF',
    DownloadLink VARCHAR(255) DEFAULT 'http://example.com/ebook',
    FOREIGN KEY (BookID) REFERENCES Book(BookID)
);
CREATE TABLE CartItems (
    CartItemID INT AUTO_INCREMENT PRIMARY KEY,
    CartID INT,
    BookID INT,
    Quantity INT DEFAULT 1 CHECK (Quantity > 0),
    FOREIGN KEY (CartID) REFERENCES Cart(CartID),
    FOREIGN KEY (BookID) REFERENCES Book(BookID)
);

-- Genre
CREATE TABLE Genre (
    GenreID INT AUTO_INCREMENT PRIMARY KEY,
    GenreName VARCHAR(100)
);

-- BookGenre (M:N link table)
CREATE TABLE BookGenre (
    BookID INT,
    GenreID INT,
    PRIMARY KEY (BookID, GenreID),
    FOREIGN KEY (BookID) REFERENCES Book(BookID),
    FOREIGN KEY (GenreID) REFERENCES Genre(GenreID)
);

-- Publisher
CREATE TABLE Publisher (
    PublisherID INT AUTO_INCREMENT PRIMARY KEY,
    NameSurname VARCHAR(100),
    Address TEXT,
    ContactNumber VARCHAR(20) DEFAULT 'N/A',
    CHECK (CHAR_LENGTH(ContactNumber) >= 5)
);

-- Order
CREATE TABLE `Order` (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT,
    OrderDate DATE,
    OrderStatus VARCHAR(50) DEFAULT 'Pending',
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- OrderDetails
CREATE TABLE OrderDetails (
    OrderDetailsID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT,
    BookID INT,
    Quantity INT DEFAULT 1,
    Subtotal DECIMAL(10,2) DEFAULT 0.00,
    CHECK (Quantity > 0),
    CHECK (Subtotal >= 0),
    FOREIGN KEY (OrderID) REFERENCES `Order`(OrderID),
    FOREIGN KEY (BookID) REFERENCES Book(BookID)
);

-- Payment
CREATE TABLE Payment (
    PaymentID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT,
    AmountPaid DECIMAL(10,2) CHECK (AmountPaid >= 0),
    PaymentMethod VARCHAR(50) DEFAULT 'Card',
    PaymentStatus VARCHAR(50) DEFAULT 'Unpaid',
    FOREIGN KEY (OrderID) REFERENCES `Order`(OrderID)
);

-- Invoice
CREATE TABLE Invoice (
    InvoiceID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT,
    InvoiceDate DATE DEFAULT (CURRENT_DATE),
    TotalAmount DECIMAL(10,2) DEFAULT 0.00,
    CHECK (TotalAmount >= 0),
    FOREIGN KEY (OrderID) REFERENCES `Order`(OrderID)
);

-- Review
CREATE TABLE Review (
    ReviewID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT,
    BookID INT,
    Rating INT DEFAULT 5 CHECK (Rating BETWEEN 1 AND 5),
    ReviewText TEXT,
    ReviewDate DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    FOREIGN KEY (BookID) REFERENCES Book(BookID)
);

-- Promotions
CREATE TABLE Promotions (
    PromotionID INT AUTO_INCREMENT PRIMARY KEY,
    PromotionName VARCHAR(100) DEFAULT 'General Promo',
    StartDate DATE,
    EndDate DATE,
    CHECK (StartDate <= EndDate)
);

-- Supertype: Discount
CREATE TABLE Discount (
    DiscountID INT AUTO_INCREMENT PRIMARY KEY,
    DiscountType ENUM('Percentage', 'FixedAmount') DEFAULT 'Percentage'
);

-- Subtype: PercentageDiscount
CREATE TABLE PercentageDiscount (
    DiscountID INT PRIMARY KEY,
    DiscountPercentage DECIMAL(5,2) DEFAULT 5.00,
    CHECK (DiscountPercentage >= 0 AND DiscountPercentage <= 100),
    FOREIGN KEY (DiscountID) REFERENCES Discount(DiscountID)
);

-- Subtype: FixedAmountDiscount
CREATE TABLE FixedAmountDiscount (
    DiscountID INT PRIMARY KEY,
    DiscountAmount DECIMAL(10,2) DEFAULT 20.00,
    CHECK (DiscountAmount >= 0),
    FOREIGN KEY (DiscountID) REFERENCES Discount(DiscountID)
);

-- Supertype: Staff
CREATE TABLE Staff (
    StaffID INT AUTO_INCREMENT PRIMARY KEY,
    NameSurname VARCHAR(100),
    Email VARCHAR(100) DEFAULT 'staff@bookstore.com',
    PhoneNumber VARCHAR(10) DEFAULT '0000000000',
    StaffType ENUM('SalesRep', 'InventoryManager'),
    CHECK (CHAR_LENGTH(PhoneNumber) = 10),
    CHECK (StaffType IN ('SalesRep', 'InventoryManager'))
);

-- Subtype: SalesRep
CREATE TABLE SalesRep (
    StaffID INT PRIMARY KEY,
    Department VARCHAR(100) DEFAULT 'General',
    FOREIGN KEY (StaffID) REFERENCES Staff(StaffID)
);

-- Subtype: InventoryManager
CREATE TABLE InventoryManager (
    StaffID INT PRIMARY KEY,
    WarehouseLocation VARCHAR(255) DEFAULT 'Main Warehouse',
    FOREIGN KEY (StaffID) REFERENCES Staff(StaffID)
);

-- Role (if needed separately)
CREATE TABLE Role (
    RoleID INT AUTO_INCREMENT PRIMARY KEY,
    RoleName VARCHAR(100) DEFAULT 'Employee'
);