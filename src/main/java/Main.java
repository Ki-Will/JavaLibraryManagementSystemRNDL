
import model.Book;
import model.Member;
import service.LibraryService;
import service.BorrowTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService();

        Book book1 = new Book("978-0134685991", "Effective Java", "Joshua Bloch", 2017);
        Book book2 = new Book("978-0596009205", "Head First Java", "Kathy Sierra", 2005);
        Book book3 = new Book("978-0132350884", "Clean Code", "Robert C. Martin", 2008);

        libraryService.addBook(book1);
        libraryService.addBook(book2);
        libraryService.addBook(book3);

        Member member1 = new Member(1, "Alice");
        Member member2 = new Member(2, "Bob");

        libraryService.addMember(member1);
        libraryService.addMember(member2);

        System.out.println("Available books: " + libraryService.getAvailableBooks());

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.execute(new BorrowTask(libraryService, 1, "978-0134685991"));
        executor.execute(new BorrowTask(libraryService, 1, "978-0596009205"));
        executor.execute(new BorrowTask(libraryService, 2, "978-0134685991"));
        executor.execute(new BorrowTask(libraryService, 2, "978-0132350884"));

        executor.shutdown();
        while (!executor.isTerminated()) {}

        System.out.println("Available books after borrowing: " + libraryService.getAvailableBooks());
        System.out.println("Member 1 borrowed books: " + member1.getBorrowedBooks());
        System.out.println("Member 2 borrowed books: " + member2.getBorrowedBooks());

        libraryService.returnBook(1, "978-0134685991");
        System.out.println("Available books after return: " + libraryService.getAvailableBooks());
    }
}
