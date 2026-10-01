package org.example.controllers;

import org.example.daos.UserDao;
import org.example.models.User;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {
    @Test
    void getsProfileRolesAndChangesPassword() {
        UserDao dao =
                mock(UserDao.class);

        User existing =
                new User(
                        "alice",
                        "old");

        when(dao.getUserByUsername("alice"))
                .thenReturn(existing);

        when(dao.getRoles("alice"))
                .thenReturn(
                        List.of("USER"));

        when(dao.updatePassword(any(User.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        ProfileController controller =
                new ProfileController(dao);

        Principal principal =
                () -> "alice";

        assertEquals(
                "alice",
                controller
                        .getProfile(principal)
                        .getUsername());

        assertEquals(
                List.of("USER"),
                controller.getRoles(
                        principal));

        User updated =
                controller.changePassword(
                        principal,
                        "\"new-password\"");

        assertEquals(
                "new-password",
                updated.getPassword());
    }
}
