package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import model.Book;
import model.Member;

public class LibraryService {
    private Map<String, Book> books;
    private Map<Integer, Member> members;
    private Lock lock;

    public LibraryService() {
        this.books = new HashMap<>();
        this.members = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public void addBook(Book book) {
        books.put(book.getISBN(), book);
    }

    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    public Book getBook(String ISBN) {
        return books.get(ISBN);
    }

    public Member getMember(int memberId) {
        return members.get(memberId);
    }

    public boolean borrowBook(int memberId, String ISBN) {
        lock.lock();
        try {
            Member member = members.get(memberId);
            Book book = books.get(ISBN);

            if (member == null || book == null) {
                return false;
            }

            if (member.getBorrowedBooks().size() >= 5) {
                return false;
            }

            if (!book.isAvailable()) {
                return false;
            }

            member.borrowBook(book);
            book.setAvailable(false);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean returnBook(int memberId, String ISBN) {
        lock.lock();
        try {
            Member member = members.get(memberId);
            Book book = books.get(ISBN);

            if (member == null || book == null) {
                return false;
            }

            member.returnBook(book);
            book.setAvailable(true);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }
    
    public void resetLibrary() {
        for (Book book : books.values()) {
            book.setAvailable(true);
        }
        for (Member member : members.values()) {
            for (Book book : new ArrayList<>(member.getBorrowedBooks())) {
                member.returnBook(book);
            }
        }
    }
}