package org.example.controllers;

import org.example.daos.UserDao;
import org.example.models.User;
import org.example.models.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> getAll() {
        return userDao.getUsers()
                .stream()
                .map(user -> new UserDto(
                        user.getUsername(),
                        userDao.getRoles(
                                user.getUsername())))
                .toList();
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDto get(
            @PathVariable String username) {

        User user =
                userDao.getUserByUsername(
                        username);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found.");
        }

        return new UserDto(
                user.getUsername(),
                userDao.getRoles(username));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserDto create(
            @RequestBody User user) {

        User created =
                userDao.createUser(user);

        return new UserDto(
                created.getUsername(),
                userDao.getRoles(
                        created.getUsername()));
    }

    @PutMapping("/{username}/password")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDto updatePassword(
            @RequestBody String password,
            @PathVariable String username) {

        User user =
                userDao.getUserByUsername(
                        username);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found.");
        }

        user.setPassword(
                password.replace("\"", ""));

        userDao.updatePassword(user);

        return new UserDto(
                username,
                userDao.getRoles(username));
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(
            @PathVariable String username) {

        if (userDao.deleteUser(username) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found.");
        }
    }

    @GetMapping("/{username}/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<String> getRoles(
            @PathVariable String username) {

        return userDao.getRoles(username);
    }

    @PostMapping("/{username}/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<String> addRole(
            @PathVariable String username,
            @RequestBody String role) {

        return userDao.addRole(
                username,
                role.replace("\"", "")
                        .trim()
                        .toUpperCase());
    }

    @DeleteMapping("/{username}/roles/{role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteRole(
            @PathVariable String username,
            @PathVariable String role) {

        if (userDao.deleteRole(
                username,
                role.toUpperCase()) == 0) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Role not found.");
        }
    }
}
