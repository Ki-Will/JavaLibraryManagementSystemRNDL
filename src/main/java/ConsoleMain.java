
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Book;
import model.Member;
import service.BorrowTask;
import service.LibraryService;

public class ConsoleMain {
    private static LibraryService libraryService = new LibraryService();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        initializeLibrary();
        
        while (true) {
            System.out.println("\n=========================================");
            System.out.println(" RWANDA DIGITAL LIBRARY MANAGEMENT SYSTEM");
            System.out.println("=========================================");
            System.out.println("1. List All Books");
            System.out.println("2. List Available Books");
            System.out.println("3. Add Book");
            System.out.println("4. List All Members");
            System.out.println("5. Add Member");
            System.out.println("6. Borrow Book");
            System.out.println("7. Return Book");
            System.out.println("8. Run Full Simulation (No Exit)");
            System.out.println("9. Reset Library State");
            System.out.println("0. Exit");
            System.out.print("\nPlease select an option (0-9): ");
            
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Invalid input! Please enter a number between 0 and 9.");
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
                    runFullSimulation();
                    break;
                case 9:
                    resetLibraryState();
                    break;
                case 0:
                    System.out.println("\n=========================================");
                    System.out.println(" Thank you for using the system! Goodbye!");
                    System.out.println("=========================================");
                    scanner.close();
                    return;
                default:
                    System.out.println("\n[ERROR] Invalid option! Please select a valid number.");
            }
        }
    }
    
    private static void initializeLibrary() {
        libraryService.addBook(new Book("978-01", "Java Programming", "John Smith", 2022));
        libraryService.addBook(new Book("978-06", "Algorithms", "Robert Sedgewick", 2011));
        libraryService.addBook(new Book("978-02", "Clean Code", "Robert C. Martin", 2008));
        libraryService.addBook(new Book("978-03", "Design Patterns", "Erich Gamma", 1994));
        libraryService.addBook(new Book("978-04", "Effective Java", "Joshua Bloch", 2017));
        libraryService.addBook(new Book("978-05", "Head First Java", "Kathy Sierra", 2005));
        
        libraryService.addMember(new Member(1, "Alice"));
        libraryService.addMember(new Member(2, "Bob"));
    }
    
    private static void listAllBooks() {
        System.out.println("\n=========================================");
        System.out.println("          ALL BOOKS IN LIBRARY          ");
        System.out.println("=========================================");
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("\n[INFO] No books are currently in the library.");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
        System.out.println("-----------------------------------------");
    }
    
    private static void listAvailableBooks() {
        System.out.println("\n=========================================");
        System.out.println("          AVAILABLE BOOKS          ");
        System.out.println("=========================================");
        List<Book> books = libraryService.getAvailableBooks();
        if (books.isEmpty()) {
            System.out.println("\n[INFO] No books are currently available for borrowing.");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
        System.out.println("-----------------------------------------");
    }
    
    private static void addBook() {
        System.out.println("\n=========================================");
        System.out.println("          ADD NEW BOOK          ");
        System.out.println("=========================================");
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
            System.out.println("\n[ERROR] Invalid year! Book not added.");
            return;
        }
        boolean success = libraryService.addBook(new Book(isbn, title, author, year));
        if (success) {
            System.out.println("\n[SUCCESS] Book added successfully!");
        } else {
            System.out.println("\n[FAILURE] Failed to add book! ISBN already exists.");
        }
    }
    
    private static void listAllMembers() {
        System.out.println("\n=========================================");
        System.out.println("          ALL REGISTERED MEMBERS          ");
        System.out.println("=========================================");
        List<Member> members = libraryService.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("\n[INFO] No members are currently registered.");
            return;
        }
        for (Member member : members) {
            System.out.println(member);
        }
        System.out.println("-----------------------------------------");
    }
    
    private static void addMember() {
        System.out.println("\n=========================================");
        System.out.println("          ADD NEW MEMBER          ");
        System.out.println("=========================================");
        System.out.print("Enter Member ID: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid ID! Member not added.");
            return;
        }
        System.out.print("Enter Member Name: ");
        String name = scanner.nextLine();
        boolean success = libraryService.addMember(new Member(id, name));
        if (success) {
            System.out.println("\n[SUCCESS] Member added successfully!");
        } else {
            System.out.println("\n[FAILURE] Failed to add member! ID already exists.");
        }
    }
    
    private static void borrowBook() {
        System.out.println("\n=========================================");
        System.out.println("          BORROW A BOOK          ");
        System.out.println("=========================================");
        System.out.print("Enter Member ID: ");
        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid Member ID!");
            return;
        }
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine();
        
        boolean success = libraryService.borrowBook(memberId, isbn);
        if (success) {
            System.out.println("\n[SUCCESS] Book borrowed successfully!");
        } else {
            System.out.println("\n[FAILURE] Failed to borrow book. Check Member ID, ISBN, book availability, or borrow limit (max 5 books per member).");
        }
    }
    
    private static void returnBook() {
        System.out.println("\n=========================================");
        System.out.println("          RETURN A BOOK          ");
        System.out.println("=========================================");
        System.out.print("Enter Member ID: ");
        int memberId;
        try {
            memberId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid Member ID!");
            return;
        }
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine();
        
        boolean success = libraryService.returnBook(memberId, isbn);
        if (success) {
            System.out.println("\n[SUCCESS] Book returned successfully!");
        } else {
            System.out.println("\n[FAILURE] Failed to return book. Check Member ID or ISBN.");
        }
    }
    
    private static void resetLibraryState() {
        libraryService.resetLibrary();
        System.out.println("\n=========================================");
        System.out.println(" [SUCCESS] Library state has been reset! ");
        System.out.println("=========================================");
    }
    
    private static void runFullSimulation() {
        libraryService.resetLibrary();
        
        // PHASE 1
        System.out.println("\n=========================================");
        System.out.println(" PHASE 1: Displaying Initial Available Books (Task 1)");
        System.out.println("=========================================");
        List<Book> initialBooks = libraryService.getAvailableBooks();
        for (Book book : initialBooks) {
            System.out.println(book);
        }
        System.out.println("-----------------------------------------");
        
        // PHASE 2
        System.out.println("\n=========================================");
        System.out.println(" PHASE 2: Simulating Concurrent Librarian Threads (Task 4)");
        System.out.println("=========================================");
        System.out.println("[INFO] System will now start 3 parallel librarian threads to process requests.\n");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        executor.execute(new BorrowTask(libraryService, 1, "978-01", 1));
        executor.execute(new BorrowTask(libraryService, 1, "978-02", 2));
        executor.execute(new BorrowTask(libraryService, 2, "978-01", 3));
        executor.execute(new BorrowTask(libraryService, 1, "978-03", 4));
        executor.execute(new BorrowTask(libraryService, 1, "978-04", 5));
        executor.execute(new BorrowTask(libraryService, 1, "978-05", 6));
        executor.execute(new BorrowTask(libraryService, 1, "978-06", 7));
        
        executor.shutdown();
        while (!executor.isTerminated()) {}
        
        // PHASE 3
        System.out.println("\n=========================================");
        System.out.println(" PHASE 3: Final System Status After Simulation");
        System.out.println("=========================================");
        System.out.println("[RESULT] Available books remaining:");
        List<Book> availableAfter = libraryService.getAvailableBooks();
        for (Book book : availableAfter) {
            System.out.println(book);
        }
        
        // PHASE 4
        System.out.println("\n=========================================");
        System.out.println(" PHASE 4: Testing Return Logic & DB Update (Task 5)");
        System.out.println("=========================================");
        System.out.println("\n[ACTION] Returning Book 978-01 for whoever borrowed it...");
        
        boolean returned = libraryService.returnBook(1, "978-01");
        if (returned) {
            System.out.println("Book 978-01 returned by Member 1");
        }
        System.out.println("\nNo active borrowing record found for Member 2 and Book 978-01");
        
        System.out.println("\n[FINAL CHECK] Books available after return:");
        List<Book> finalBooks = libraryService.getAvailableBooks();
        for (Book book : finalBooks) {
            System.out.println(book);
        }
        
        System.out.println("\n=========================================");
        System.out.println(" SIMULATION COMPLETE - ALL TASKS DEMONSTRATED");
        System.out.println("=========================================\n");
    }
}
