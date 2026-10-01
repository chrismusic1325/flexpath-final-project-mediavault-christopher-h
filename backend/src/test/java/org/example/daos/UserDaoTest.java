package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
class UserDaoTest {
    private JdbcTemplate jdbc;
    private PasswordEncoder encoder;
    private UserDao dao;

    @BeforeEach
    void setup() {
        jdbc =
                mock(JdbcTemplate.class);

        encoder =
                mock(PasswordEncoder.class);

        dao =
                new UserDao(
                        mock(DataSource.class),
                        encoder);

        ReflectionTestUtils.setField(
                dao,
                "jdbcTemplate",
                jdbc);
    }

    @Test
    void getsUsersAndUserByName() {
        User user =
                new User(
                        "alice",
                        "encoded");

        when(jdbc.query(
                anyString(),
                any(RowMapper.class)))
                .thenReturn(
                        List.of(user));

        when(jdbc.queryForObject(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(user)
                .thenThrow(
                        new EmptyResultDataAccessException(
                                1));

        assertEquals(
                1,
                dao.getUsers().size());

        assertSame(
                user,
                dao.getUserByUsername(
                        "alice"));

        assertNull(
                dao.getUserByUsername(
                        "missing"));
    }

    @Test
    void createValidatesAndCreatesUser() {
        assertThrows(
                DaoException.class,
                () ->
                        dao.createUser(
                                new User()));

        when(encoder.encode("plain"))
                .thenReturn("encoded");

        User stored =
                new User(
                        "alice",
                        "encoded");

        when(jdbc.queryForObject(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(stored);

        User result =
                dao.createUser(
                        new User(
                                "alice",
                                "plain"));

        assertEquals(
                "alice",
                result.getUsername());

        verify(jdbc, times(2))
                .update(
                        anyString(),
                        any(Object[].class));
    }

    @Test
    void createWrapsDatabaseError() {
        when(encoder.encode("plain"))
                .thenReturn("encoded");

        doThrow(
                new DataAccessResourceFailureException(
                        "database"))
                .when(jdbc)
                .update(
                        anyString(),
                        any(Object[].class));

        assertThrows(
                DaoException.class,
                () ->
                        dao.createUser(
                                new User(
                                        "alice",
                                        "plain")));
    }

    @Test
    void updatePasswordHandlesSuccessAndMissingUser() {
        when(encoder.encode("new"))
                .thenReturn("encoded");

        User user =
                new User(
                        "alice",
                        "new");

        when(jdbc.update(
                startsWith("UPDATE"),
                any(Object[].class)))
                .thenReturn(1);

        when(jdbc.queryForObject(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(
                        new User(
                                "alice",
                                "encoded"));

        assertEquals(
                "alice",
                dao.updatePassword(user)
                        .getUsername());

        reset(jdbc);

        when(encoder.encode("new"))
                .thenReturn("encoded");

        when(jdbc.update(
                anyString(),
                any(Object[].class)))
                .thenReturn(0);

        assertThrows(
                DaoException.class,
                () ->
                        dao.updatePassword(
                                user));
    }

    @Test
    void roleAndDeleteOperationsDelegate() {
        when(jdbc.queryForList(
                anyString(),
                eq(String.class),
                any(Object[].class)))
                .thenReturn(
                        List.of("USER"));

        assertEquals(
                List.of("USER"),
                dao.getRoles(
                        "alice"));

        assertEquals(
                List.of("USER"),
                dao.addRole(
                        "alice",
                        "ADMIN"));

        dao.deleteRole(
                "alice",
                "ADMIN");

        dao.deleteUser(
                "alice");

        verify(
                jdbc,
                atLeast(3))
                .update(
                        anyString(),
                        any(Object[].class));
    }

    @Test
    void duplicateRoleIsTolerated() {
        when(jdbc.queryForList(
                anyString(),
                eq(String.class),
                any(Object[].class)))
                .thenReturn(
                        List.of("USER"));

        doThrow(
                new DataAccessResourceFailureException(
                        "duplicate"))
                .when(jdbc)
                .update(
                        contains(
                                "INSERT INTO roles"),
                        any(Object[].class));

        assertEquals(
                List.of("USER"),
                dao.addRole(
                        "alice",
                        "USER"));
    }

    @Test
    void rowMapperMapsUser() throws Exception {
        ResultSet rs =
                mock(ResultSet.class);

        when(rs.getString("username"))
                .thenReturn("alice");

        when(rs.getString("password"))
                .thenReturn("encoded");

        User user =
                ReflectionTestUtils.invokeMethod(
                        dao,
                        "mapToUser",
                        rs,
                        0);

        assertNotNull(user);
        assertEquals(
                "alice",
                user.getUsername());
        assertEquals(
                "encoded",
                user.getPassword());
    }
}
