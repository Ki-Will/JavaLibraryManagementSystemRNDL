
package service;

import model.Book;
import model.Member;

public class BorrowTask implements Runnable {
    private LibraryService libraryService;
    private int memberId;
    private String ISBN;
    private int taskNumber;

    // Constructor for ConsoleMain simulation
    public BorrowTask(LibraryService libraryService, int memberId, String ISBN, int taskNumber) {
        this.libraryService = libraryService;
        this.memberId = memberId;
        this.ISBN = ISBN;
        this.taskNumber = taskNumber;
    }

    // Backward compatible constructor for Main.java
    public BorrowTask(LibraryService libraryService, int memberId, String ISBN) {
        this(libraryService, memberId, ISBN, 0);
    }

    @Override
    public void run() {
        if (taskNumber > 0) {
            System.out.println(Thread.currentThread().getName() + " is processing request for Member " + memberId + ", Book " + ISBN);
            boolean success = libraryService.borrowBook(memberId, ISBN);
            if (success) {
                System.out.println("[SUCCESS] [pool-1-thread-" + Thread.currentThread().getName().split("-")[3] + "] Task " + taskNumber + " (Thread Safety): DB Persistent Transaction Completed. Member " + memberId + " successfully borrowed " + ISBN);
            } else {
                Book book = libraryService.getBook(ISBN);
                Member member = libraryService.getMember(memberId);
                String reason = "";
                if (book == null || member == null) {
                    reason = "Invalid member or book.";
                } else if (!book.isAvailable()) {
                    reason = "Book " + ISBN + " is already borrowed. Request denied.";
                } else if (member.getBorrowedBooks().size() >= 5) {
                    reason = "Member " + memberId + " already borrowed the limit.";
                }
                System.out.println("[FAILURE] [pool-1-thread-" + Thread.currentThread().getName().split("-")[3] + "] Task " + taskNumber + " (Thread Safety): " + reason);
            }
        } else {
            // Original behavior for Main.java
            boolean success = libraryService.borrowBook(memberId, ISBN);
            if (success) {
                System.out.println(Thread.currentThread().getName() + " successfully borrowed book " + ISBN + " for member " + memberId);
            } else {
                System.out.println(Thread.currentThread().getName() + " failed to borrow book " + ISBN + " for member " + memberId);
            }
        }
    }
}
