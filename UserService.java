package com.example.perpus;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserById(Long userId) {
        String sql = "SELECT * FROM user WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            return user;
        }, userId);
    }

    public void borrowBook(Long userId, Long bookId) {
        User user = getUserById(userId);
        user.borrow(jdbcTemplate, bookId);
    }

    public void returnBook(Long transactionId) {
        String sql = "SELECT user_id FROM borrow_transaction WHERE id = ?";
        Long userId = jdbcTemplate.queryForObject(sql, Long.class, transactionId);
        User user = getUserById(userId);
        user.returnBook(jdbcTemplate, transactionId);
    }

    /* 
    public Objects getActiveTransactions() {
        String sql = "SELECT * FROM borrow_transaction";
        return null;
    }
     */
    public List<BorrowTransaction> getActiveTransactions() {
        try {
            String sql = "SELECT id, user_id, book_id, borrow_date, return_date "
                    + "FROM borrow_transaction "
                    + "WHERE return_date IS NULL";

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                BorrowTransaction transaction = new BorrowTransaction();
                transaction.setId(rs.getLong("id"));
                transaction.setUserId(rs.getLong("user_id"));
                transaction.setBookId(rs.getLong("book_id"));

                // Handle dates (using LocalDate instead of LocalDateTime)
                transaction.setBorrowDate(rs.getDate("borrow_date").toLocalDate());

                // return_date might be null (which is what we're filtering for)
                if (rs.getDate("return_date") != null) {
                    transaction.setReturnDate(rs.getDate("return_date").toLocalDate());
                }

                return transaction;
            });
        } catch (EmptyResultDataAccessException e) {
            // Return empty list if no results found
            return Collections.emptyList();
        }
    }

}
