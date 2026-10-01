package org.example.services;

import org.example.daos.UserDao;
import org.example.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    @Test
    void loadsExistingUserAndRoles() {
        UserDao dao =
                mock(UserDao.class);

        when(dao.getUserByUsername("alice"))
                .thenReturn(
                        new User(
                                "alice",
                                "encoded"));

        when(dao.getRoles("alice"))
                .thenReturn(
                        List.of(
                                "USER",
                                "ADMIN"));

        CustomUserDetailsService service =
                new CustomUserDetailsService(
                        dao);

        UserDetails details =
                service.loadUserByUsername(
                        "alice");

        assertEquals(
                "alice",
                details.getUsername());

        assertEquals(
                2,
                details.getAuthorities()
                        .size());

        assertTrue(
                details.isEnabled());
    }

    @Test
    void rejectsUnknownUser() {
        UserDao dao =
                mock(UserDao.class);

        when(dao.getUserByUsername("missing"))
                .thenReturn(null);

        CustomUserDetailsService service =
                new CustomUserDetailsService(
                        dao);

        assertThrows(
                UsernameNotFoundException.class,
                () ->
                        service.loadUserByUsername(
                                "missing"));
    }
}
