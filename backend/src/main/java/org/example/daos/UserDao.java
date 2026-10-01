package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserDao(DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return jdbcTemplate.query(
                "SELECT username, password FROM users ORDER BY username",
                this::mapToUser);
    }

    public User getUserByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT username, password FROM users WHERE username = ?",
                    this::mapToUser,
                    username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User createUser(User user) {
        if (user == null ||
                user.getUsername() == null ||
                user.getUsername().isBlank() ||
                user.getPassword() == null ||
                user.getPassword().isBlank()) {
            throw new DaoException("Username and password are required.");
        }

        try {
            String encodedPassword =
                    passwordEncoder.encode(user.getPassword());

            jdbcTemplate.update(
                    "INSERT INTO users (username, password) VALUES (?, ?)",
                    user.getUsername(),
                    encodedPassword);

            jdbcTemplate.update(
                    "INSERT INTO roles (username, role) VALUES (?, 'USER')",
                    user.getUsername());

            return getUserByUsername(user.getUsername());

        } catch (DataAccessException e) {
            throw new DaoException("Unable to create user.");
        }
    }

    public User updatePassword(User user) {
        String encodedPassword =
                passwordEncoder.encode(user.getPassword());

        int count = jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE username = ?",
                encodedPassword,
                user.getUsername());

        if (count == 0) {
            throw new DaoException("User was not found.");
        }

        return getUserByUsername(user.getUsername());
    }

    public int deleteUser(String username) {
        return jdbcTemplate.update(
                "DELETE FROM users WHERE username = ?",
                username);
    }

    public List<String> getRoles(String username) {
        return jdbcTemplate.queryForList(
                "SELECT role FROM roles WHERE username = ? ORDER BY role",
                String.class,
                username);
    }

    public List<String> addRole(String username, String role) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO roles (username, role) VALUES (?, ?)",
                    username,
                    role);
        } catch (DataAccessException ignored) {
        }

        return getRoles(username);
    }

    public int deleteRole(String username, String role) {
        return jdbcTemplate.update(
                "DELETE FROM roles WHERE username = ? AND role = ?",
                username,
                role);
    }

    private User mapToUser(ResultSet resultSet, int row)
            throws SQLException {

        return new User(
                resultSet.getString("username"),
                resultSet.getString("password"));
    }
}
