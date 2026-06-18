
package service;


public class BorrowTask implements Runnable {
    private LibraryService libraryService;
    private int memberId;
    private String ISBN;

    public BorrowTask(LibraryService libraryService, int memberId, String ISBN) {
        this.libraryService = libraryService;
        this.memberId = memberId;
        this.ISBN = ISBN;
    }

    @Override
    public void run() {
        boolean success = libraryService.borrowBook(memberId, ISBN);
        if (success) {
            System.out.println(Thread.currentThread().getName() + " successfully borrowed book " + ISBN + " for member " + memberId);
        } else {
            System.out.println(Thread.currentThread().getName() + " failed to borrow book " + ISBN + " for member " + memberId);
        }
    }
}
