package org.example.controllers;

import org.example.daos.UserDao;
import org.example.models.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {
    private final UserDao userDao;

    public ProfileController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping
    public User getProfile(Principal principal) {
        return userDao.getUserByUsername(principal.getName());
    }

    @GetMapping("/roles")
    public List<String> getRoles(Principal principal) {
        return userDao.getRoles(principal.getName());
    }

    @PutMapping("/change-password")
    public User changePassword(
            Principal principal,
            @RequestBody String newPassword) {

        User user =
                userDao.getUserByUsername(
                        principal.getName());

        user.setPassword(
                newPassword.replace("\"", ""));

        return userDao.updatePassword(user);
    }
}
