package com.example.perpus;

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
}

