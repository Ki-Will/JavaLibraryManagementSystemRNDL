import model.Book;
import model.Member;
import service.LibraryService;
import service.BorrowTask;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleMain {
    private static LibraryService libraryService = new LibraryService();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        initializeLibrary();
        
        while (true) {
            System.out.println("\n===== Rwanda Digital Library Management System =====");
            System.out.println("1. List All Books");
            System.out.println("2. List Available Books");
            System.out.println("3. Add Book");
            System.out.println("4. List All Members");
            System.out.println("5. Add Member");
            System.out.println("6. Borrow Book");
            System.out.println("7. Return Book");
            System.out.println("8. Simulate Concurrent Borrowing");
            System.out.println("0. Exit");
            System.out.print("Select option: ");
            
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }
            
            switch (choice) {
                case 1:
                    listAllBooks();
                    break;
                case 2:
                    listAvailableBooks();
                    break;
                case 3:
                    addBook();
                    break;
                case 4:
                    listAllMembers();
                    break;
                case 5:
                    addMember();
                    break;
                case 6:
                    borrowBook();
                    break;
                case 7:
                    returnBook();
                    break;
                case 8:
                    simulateConcurrentBorrowing();
                    break;
                case 0:
                    System.out.println("Exiting system...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private static void initializeLibrary() {
        libraryService.addBook(new Book("978-0134685991", "Effective Java", "Joshua Bloch", 2017));
        libraryService.addBook(new Book("978-0596009205", "Head First Java", "Kathy Sierra", 2005));
        libraryService.addBook(new Book("978-0132350884", "Clean Code", "Robert C. Martin", 2008));
        libraryService.addBook(new Book("978-0201633610", "Design Patterns", "Erich Gamma", 1994));
        
        libraryService.addMember(new Member(1, "Alice"));
        libraryService.addMember(new Member(2, "Bob"));
        libraryService.addMember(new Member(3, "Charlie"));
    }
    
    private static void listAllBooks() {
        System.out.println("\n--- All Books ---");
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }
    
    private static void listAvailableBooks() {
        System.out.println("\n--- Available Books ---");
        List<Book> books = libraryService.getAvailableBooks();
        if (books.isEmpty()) {
            System.out.println("No available books.");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }
    
    private static void addBook() {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter Publication Year: ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year. Book not added.");
            return;
        }
        libraryService.addBook(new Book(isbn, title, author, year));
        System.out.println("Book added successfully!");
    }
    
    private static void listAllMembers() {
        System.out.println("\n--- All Members ---");
        List<Member> members = libraryService.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        for (Member member : members) {
            System.out.println(member);
        }
    }
    
    private static void addMember() {
        System.out.println("\n--- Add New Member ---");
        System.out.print("Enter Member ID: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Member not added.");
            return;
        }
        System.out.print("Enter Member Name: ");
        String name = scanner.nextLine();
        libraryService.addMember(new Member(id, name));
        System.out.println("Member added successfully!");
    }
    
    private static void borrowBook() {
        System.out.println("\n--- Borrow Book ---");
        System.out.print("Enter Member ID: ");
        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Member ID.");
            return;
        }
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine();
        
        boolean success = libraryService.borrowBook(memberId, isbn);
        if (success) {
            System.out.println("Book borrowed successfully!");
        } else {
            System.out.println("Failed to borrow book. Check Member ID, ISBN, book availability, or borrow limit (max 5 books per member).");
        }
    }
    
    private static void returnBook() {
        System.out.println("\n--- Return Book ---");
        System.out.print("Enter Member ID: ");
        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Member ID.");
            return;
        }
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine();
        
        boolean success = libraryService.returnBook(memberId, isbn);
        if (success) {
            System.out.println("Book returned successfully!");
        } else {
            System.out.println("Failed to return book. Check Member ID or ISBN.");
        }
    }
    
    private static void simulateConcurrentBorrowing() {
        System.out.println("\n--- Simulating Concurrent Borrowing ---");
        System.out.print("Enter number of librarian threads (1-10): ");
        int threadCount;
        try {
            threadCount = Integer.parseInt(scanner.nextLine());
            if (threadCount < 1 || threadCount > 10) {
                System.out.println("Please enter a number between 1 and 10.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        executor.execute(new BorrowTask(libraryService, 1, "978-0134685991"));
        executor.execute(new BorrowTask(libraryService, 2, "978-0134685991"));
        executor.execute(new BorrowTask(libraryService, 1, "978-0596009205"));
        executor.execute(new BorrowTask(libraryService, 3, "978-0132350884"));
        executor.execute(new BorrowTask(libraryService, 2, "978-0201633610"));
        executor.execute(new BorrowTask(libraryService, 3, "978-0596009205"));
        
        executor.shutdown();
        while (!executor.isTerminated()) {}
        
        System.out.println("\n--- Concurrent Borrowing Complete ---");
        System.out.println("Available Books After Simulation:");
        listAvailableBooks();
        System.out.println("\nMembers After Simulation:");
        listAllMembers();
    }
}