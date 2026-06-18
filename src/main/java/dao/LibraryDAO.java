
package dao;

import util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDate;

public class LibraryDAO {
    public boolean recordBorrow(int memberId, String ISBN) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Check if member exists (FK validation)
            String checkMemberSql = "SELECT member_id FROM Members WHERE member_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkMemberSql)) {
                pstmt.setInt(1, memberId);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
            }

            // Check if book exists and is available (FK + availability)
            String checkBookSql = "SELECT ISBN FROM Books WHERE ISBN = ? AND is_available = TRUE";
            try (PreparedStatement pstmt = conn.prepareStatement(checkBookSql)) {
                pstmt.setString(1, ISBN);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
            }

            // Check if member has <=4 borrowed books
            String countBorrowedSql = "SELECT COUNT(*) as count FROM Borrowing_Records WHERE member_id = ? AND return_date IS NULL";
            try (PreparedStatement pstmt = conn.prepareStatement(countBorrowedSql)) {
                pstmt.setInt(1, memberId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count >= 5) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Update book availability
            String updateBookSql = "UPDATE Books SET is_available = FALSE WHERE ISBN = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                pstmt.setString(1, ISBN);
                pstmt.executeUpdate();
            }

            // Insert borrowing record with today's date
            String insertRecordSql = "INSERT INTO Borrowing_Records (member_id, ISBN, borrow_date) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertRecordSql)) {
                pstmt.setInt(1, memberId);
                pstmt.setString(2, ISBN);
                pstmt.setDate(3, Date.valueOf(LocalDate.now()));
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }

    public boolean recordReturn(int memberId, String ISBN) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Check if there's an active borrowing record for this member and book
            String checkRecordSql = "SELECT record_id, borrow_date FROM Borrowing_Records WHERE member_id = ? AND ISBN = ? AND return_date IS NULL";
            LocalDate borrowDate = null;
            try (PreparedStatement pstmt = conn.prepareStatement(checkRecordSql)) {
                pstmt.setInt(1, memberId);
                pstmt.setString(2, ISBN);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                borrowDate = rs.getDate("borrow_date").toLocalDate();
            }

            // Date validation: return date can't be before borrow date
            LocalDate returnDate = LocalDate.now();
            if (returnDate.isBefore(borrowDate)) {
                conn.rollback();
                return false;
            }

            // Update book availability
            String updateBookSql = "UPDATE Books SET is_available = TRUE WHERE ISBN = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                pstmt.setString(1, ISBN);
                pstmt.executeUpdate();
            }

            // Update return date
            String updateRecordSql = "UPDATE Borrowing_Records SET return_date = ? WHERE member_id = ? AND ISBN = ? AND return_date IS NULL";
            try (PreparedStatement pstmt = conn.prepareStatement(updateRecordSql)) {
                pstmt.setDate(1, Date.valueOf(returnDate));
                pstmt.setInt(2, memberId);
                pstmt.setString(3, ISBN);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }
}
