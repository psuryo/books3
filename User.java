package com.example.perpus;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;


public class User {

    private Long id;
    private String name;

    // Borrow method encapsulates the logic
    public void borrow(JdbcTemplate jdbcTemplate, Long bookId) {
        // Check if the book is available
        String checkBookSql = "SELECT available FROM book WHERE id = ?";
        Boolean available = jdbcTemplate.queryForObject(checkBookSql, Boolean.class, bookId);
        if (available == null || !available) {
            throw new RuntimeException("Book is not available or does not exist");
        }

        // Mark the book as unavailable
        String updateBookSql = "UPDATE book SET available = FALSE WHERE id = ?";
        jdbcTemplate.update(updateBookSql, bookId);

        // Insert a new borrow transaction
        String insertTransactionSql = "INSERT INTO borrow_transaction (user_id, book_id, borrow_date) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertTransactionSql, id, bookId, LocalDate.now());
    }

    public void returnBook(JdbcTemplate jdbcTemplate, Long transactionId) {
        // Check the transaction and get the book ID
        String checkTransactionSql = "SELECT book_id, return_date FROM borrow_transaction WHERE id = ?";
        Map<String, Object> transaction = jdbcTemplate.queryForMap(checkTransactionSql, transactionId);
        if (transaction.get("return_date") != null) {
            throw new RuntimeException("Book is already returned");
        }

        Long bookId = (Long) transaction.get("book_id");

        // Mark the book as available
        String updateBookSql = "UPDATE book SET available = TRUE WHERE id = ?";
        jdbcTemplate.update(updateBookSql, bookId);

        // Update the return date of the transaction
        String updateTransactionSql = "UPDATE borrow_transaction SET return_date = ? WHERE id = ?";
        jdbcTemplate.update(updateTransactionSql, LocalDate.now(), transactionId);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
