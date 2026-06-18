
-- Create Books table
CREATE TABLE IF NOT EXISTS Books (
    ISBN VARCHAR(20) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publication_year INT,
    is_available BOOLEAN DEFAULT TRUE
);

-- Create Members table
CREATE TABLE IF NOT EXISTS Members (
    member_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create Borrowing_Records table
CREATE TABLE IF NOT EXISTS Borrowing_Records (
    record_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    ISBN VARCHAR(20) NOT NULL,
    borrow_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (member_id) REFERENCES Members(member_id),
    FOREIGN KEY (ISBN) REFERENCES Books(ISBN)
);

-- Insert sample data
INSERT INTO Books (ISBN, title, author, publication_year) VALUES
('978-0134685991', 'Effective Java', 'Joshua Bloch', 2017),
('978-0596009205', 'Head First Java', 'Kathy Sierra', 2005),
('978-0132350884', 'Clean Code', 'Robert C. Martin', 2008);

INSERT INTO Members (name) VALUES
('Alice'),
('Bob'),
('Charlie');
