package org.example.controllers;

import org.example.daos.MediaItemDao;
import org.example.daos.UserDao;
import org.example.models.MediaItem;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/items")
public class MediaItemController {
    private final MediaItemDao mediaItemDao;
    private final UserDao userDao;

    public MediaItemController(
            MediaItemDao mediaItemDao,
            UserDao userDao) {

        this.mediaItemDao = mediaItemDao;
        this.userDao = userDao;
    }

    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public List<MediaItem> publicItems(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String creator,
            @RequestParam(required = false) String mediaType,
            @RequestParam(defaultValue = "createdDate")
            String sortBy,
            @RequestParam(defaultValue = "desc")
            String direction) {

        return mediaItemDao.search(
                null,
                false,
                title,
                creator,
                mediaType,
                sortBy,
                direction);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<MediaItem> items(
            Principal principal,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String creator,
            @RequestParam(required = false) String mediaType,
            @RequestParam(defaultValue = "createdDate")
            String sortBy,
            @RequestParam(defaultValue = "desc")
            String direction) {

        return mediaItemDao.search(
                principal.getName(),
                isAdmin(principal.getName()),
                title,
                creator,
                mediaType,
                sortBy,
                direction);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public MediaItem get(
            @PathVariable long id,
            Principal principal) {

        MediaItem item = requireItem(id);

        if (item.isPublic()) {
            return item;
        }

        if (principal != null &&
                (item.getOwner().equals(
                        principal.getName()) ||
                 isAdmin(principal.getName()))) {
            return item;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public MediaItem create(
            @RequestBody MediaItem item,
            Principal principal) {

        validate(item);

        item.setId(null);
        item.setOwner(principal.getName());
        item.setCreatedDate(null);

        if (item.getCreator() == null ||
                item.getCreator().isBlank()) {
            item.setCreator(principal.getName());
        }

        return mediaItemDao.create(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public MediaItem update(
            @PathVariable long id,
            @RequestBody MediaItem item,
            Principal principal) {

        MediaItem existing = requireItem(id);

        ensureOwnerOrAdmin(
                existing.getOwner(),
                principal);

        validate(item);

        item.setId(id);
        item.setOwner(existing.getOwner());
        item.setCreatedDate(
                existing.getCreatedDate());

        return mediaItemDao.update(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void delete(
            @PathVariable long id,
            Principal principal) {

        MediaItem existing = requireItem(id);

        ensureOwnerOrAdmin(
                existing.getOwner(),
                principal);

        mediaItemDao.delete(id);
    }

    private MediaItem requireItem(long id) {
        MediaItem item = mediaItemDao.getById(id);

        if (item == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Media item not found.");
        }

        return item;
    }

    private void validate(MediaItem item) {
        if (item.getTitle() == null ||
                item.getTitle().isBlank() ||
                item.getMediaType() == null ||
                item.getMediaType().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Title and media type are required.");
        }
    }

    private void ensureOwnerOrAdmin(
            String owner,
            Principal principal) {

        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED);
        }

        String username = principal.getName();

        if (!owner.equals(username) &&
                !isAdmin(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN);
        }
    }

    private boolean isAdmin(String username) {
        return userDao.getRoles(username)
                .stream()
                .anyMatch(
                        "ADMIN"::equalsIgnoreCase);
    }
}
