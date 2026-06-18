
package dao;

import util.DatabaseUtil;
import java.sql.*;

public class LibraryDAO {
    public void recordBorrow(int memberId, String ISBN) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String updateBookSql = "UPDATE Books SET is_available = FALSE WHERE ISBN = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                pstmt.setString(1, ISBN);
                pstmt.executeUpdate();
            }

            String insertRecordSql = "INSERT INTO Borrowing_Records (member_id, ISBN, borrow_date) VALUES (?, ?, CURDATE())";
            try (PreparedStatement pstmt = conn.prepareStatement(insertRecordSql)) {
                pstmt.setInt(1, memberId);
                pstmt.setString(2, ISBN);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }

    public void recordReturn(int memberId, String ISBN) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String updateBookSql = "UPDATE Books SET is_available = TRUE WHERE ISBN = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                pstmt.setString(1, ISBN);
                pstmt.executeUpdate();
            }

            String updateRecordSql = "UPDATE Borrowing_Records SET return_date = CURDATE() WHERE member_id = ? AND ISBN = ? AND return_date IS NULL";
            try (PreparedStatement pstmt = conn.prepareStatement(updateRecordSql)) {
                pstmt.setInt(1, memberId);
                pstmt.setString(2, ISBN);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }
}
