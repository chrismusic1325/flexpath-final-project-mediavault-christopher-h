package org.example.controllers;

import org.example.daos.UserDao;
import org.example.models.User;
import org.example.models.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Test
    void listsAndGetsUsers() {
        UserDao dao =
                mock(UserDao.class);

        when(dao.getUsers())
                .thenReturn(
                        List.of(
                                new User(
                                        "alice",
                                        "encoded")));

        when(dao.getRoles("alice"))
                .thenReturn(
                        List.of("USER"));

        when(dao.getUserByUsername("alice"))
                .thenReturn(
                        new User(
                                "alice",
                                "encoded"));

        UserController controller =
                new UserController(dao);

        List<UserDto> users =
                controller.getAll();

        assertEquals(1, users.size());
        assertEquals(
                "alice",
                users.get(0).getUsername());

        assertEquals(
                "alice",
                controller.get("alice")
                        .getUsername());
    }

    @Test
    void getRejectsUnknownUser() {
        UserDao dao =
                mock(UserDao.class);

        when(dao.getUserByUsername("missing"))
                .thenReturn(null);

        UserController controller =
                new UserController(dao);

        assertThrows(
                ResponseStatusException.class,
                () -> controller.get(
                        "missing"));
    }

    @Test
    void createsUpdatesAndDeletesUser() {
        UserDao dao =
                mock(UserDao.class);

        User created =
                new User(
                        "alice",
                        "encoded");

        when(dao.createUser(any(User.class)))
                .thenReturn(created);

        when(dao.getRoles("alice"))
                .thenReturn(
                        List.of("USER"));

        when(dao.getUserByUsername("alice"))
                .thenReturn(created);

        when(dao.updatePassword(any(User.class)))
                .thenReturn(created);

        when(dao.deleteUser("alice"))
                .thenReturn(1);

        UserController controller =
                new UserController(dao);

        assertEquals(
                "alice",
                controller.create(
                        new User(
                                "alice",
                                "plain"))
                        .getUsername());

        controller.updatePassword(
                "\"new\"",
                "alice");

        controller.delete("alice");

        verify(dao).deleteUser("alice");
    }

    @Test
    void missingDeleteAndPasswordUpdateFail() {
        UserDao dao =
                mock(UserDao.class);

        when(dao.getUserByUsername("missing"))
                .thenReturn(null);

        when(dao.deleteUser("missing"))
                .thenReturn(0);

        UserController controller =
                new UserController(dao);

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.updatePassword(
                                "new",
                                "missing"));

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.delete(
                                "missing"));
    }

    @Test
    void roleOperationsDelegateToDao() {
        UserDao dao =
                mock(UserDao.class);

        when(dao.getRoles("alice"))
                .thenReturn(
                        List.of("USER"));

        when(dao.addRole(
                "alice",
                "ADMIN"))
                .thenReturn(
                        List.of(
                                "USER",
                                "ADMIN"));

        when(dao.deleteRole(
                "alice",
                "ADMIN"))
                .thenReturn(1);

        UserController controller =
                new UserController(dao);

        assertEquals(
                List.of("USER"),
                controller.getRoles(
                        "alice"));

        assertEquals(
                2,
                controller.addRole(
                        "alice",
                        "\"admin\"")
                        .size());

        controller.deleteRole(
                "alice",
                "admin");
    }

    @Test
    void deletingMissingRoleFails() {
        UserDao dao =
                mock(UserDao.class);

        when(dao.deleteRole(
                "alice",
                "ADMIN"))
                .thenReturn(0);

        UserController controller =
                new UserController(dao);

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.deleteRole(
                                "alice",
                                "admin"));
    }
}
