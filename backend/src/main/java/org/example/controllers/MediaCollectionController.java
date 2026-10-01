package org.example.controllers;

import org.example.daos.MediaCollectionDao;
import org.example.daos.MediaItemDao;
import org.example.daos.UserDao;
import org.example.models.AddItemToCollectionRequest;
import org.example.models.MediaCollection;
import org.example.models.MediaItem;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/collections")
public class MediaCollectionController {
    private final MediaCollectionDao collectionDao;
    private final MediaItemDao itemDao;
    private final UserDao userDao;

    public MediaCollectionController(
            MediaCollectionDao collectionDao,
            MediaItemDao itemDao,
            UserDao userDao) {

        this.collectionDao = collectionDao;
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public List<MediaCollection> publicCollections(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String owner,
            @RequestParam(defaultValue = "createdDate")
            String sortBy,
            @RequestParam(defaultValue = "desc")
            String direction) {

        return collectionDao.search(
                null,
                false,
                name,
                owner,
                sortBy,
                direction);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<MediaCollection> collections(
            Principal principal,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String owner,
            @RequestParam(defaultValue = "createdDate")
            String sortBy,
            @RequestParam(defaultValue = "desc")
            String direction) {

        return collectionDao.search(
                principal.getName(),
                isAdmin(principal.getName()),
                name,
                owner,
                sortBy,
                direction);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public MediaCollection get(
            @PathVariable long id,
            Principal principal) {

        MediaCollection collection =
                requireCollection(id);

        ensureReadable(
                collection,
                principal);

        return collection;
    }

    @GetMapping("/{id}/items")
    @PreAuthorize("permitAll()")
    public List<MediaItem> items(
            @PathVariable long id,
            Principal principal) {

        MediaCollection collection =
                requireCollection(id);

        ensureReadable(
                collection,
                principal);

        return collectionDao.getItems(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public MediaCollection create(
            @RequestBody MediaCollection collection,
            Principal principal) {

        validate(collection);

        collection.setId(null);
        collection.setOwner(
                principal.getName());
        collection.setCreatedDate(null);

        return collectionDao.create(
                collection);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public MediaCollection update(
            @PathVariable long id,
            @RequestBody MediaCollection collection,
            Principal principal) {

        MediaCollection existing =
                requireCollection(id);

        ensureOwnerOrAdmin(
                existing.getOwner(),
                principal);

        validate(collection);

        collection.setId(id);
        collection.setOwner(
                existing.getOwner());
        collection.setCreatedDate(
                existing.getCreatedDate());

        return collectionDao.update(
                collection);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void delete(
            @PathVariable long id,
            Principal principal) {

        MediaCollection existing =
                requireCollection(id);

        ensureOwnerOrAdmin(
                existing.getOwner(),
                principal);

        collectionDao.delete(id);
    }

    @PostMapping("/{id}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void addItem(
            @PathVariable long id,
            @RequestBody
            AddItemToCollectionRequest request,
            Principal principal) {

        MediaCollection collection =
                requireCollection(id);

        ensureOwnerOrAdmin(
                collection.getOwner(),
                principal);

        if (request.getItemId() == null ||
                itemDao.getById(
                        request.getItemId()) == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Media item not found.");
        }

        collectionDao.addItem(
                id,
                request.getItemId());
    }

    @DeleteMapping(
            "/{collectionId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void removeItem(
            @PathVariable long collectionId,
            @PathVariable long itemId,
            Principal principal) {

        MediaCollection collection =
                requireCollection(
                        collectionId);

        ensureOwnerOrAdmin(
                collection.getOwner(),
                principal);

        collectionDao.removeItem(
                collectionId,
                itemId);
    }

    private MediaCollection requireCollection(
            long id) {

        MediaCollection collection =
                collectionDao.getById(id);

        if (collection == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Collection not found.");
        }

        return collection;
    }

    private void validate(
            MediaCollection collection) {

        if (collection.getName() == null ||
                collection.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Collection name is required.");
        }
    }

    private void ensureReadable(
            MediaCollection collection,
            Principal principal) {

        if (collection.isPublic()) {
            return;
        }

        if (principal != null &&
                (collection.getOwner().equals(
                        principal.getName()) ||
                 isAdmin(principal.getName()))) {
            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN);
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
