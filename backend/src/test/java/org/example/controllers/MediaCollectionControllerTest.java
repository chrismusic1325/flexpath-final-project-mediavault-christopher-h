package org.example.controllers;

import org.example.daos.MediaCollectionDao;
import org.example.daos.MediaItemDao;
import org.example.daos.UserDao;
import org.example.models.AddItemToCollectionRequest;
import org.example.models.MediaCollection;
import org.example.models.MediaItem;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MediaCollectionControllerTest {
    private Principal principal(String name) {
        return () -> name;
    }

    private MediaCollection collection(
            long id,
            String owner,
            boolean isPublic) {

        MediaCollection collection =
                new MediaCollection();

        collection.setId(id);
        collection.setName("Favorites");
        collection.setDescription("Description");
        collection.setPublic(isPublic);
        collection.setOwner(owner);

        return collection;
    }

    @Test
    void publicAndAuthenticatedSearchDelegate() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(collectionDao.search(
                any(),
                anyBoolean(),
                any(),
                any(),
                any(),
                any()))
                .thenReturn(List.of());

        when(userDao.getRoles("admin"))
                .thenReturn(
                        List.of("ADMIN"));

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        itemDao,
                        userDao);

        assertNotNull(
                controller.publicCollections(
                        "fav",
                        null,
                        "name",
                        "asc"));

        assertNotNull(
                controller.collections(
                        principal("admin"),
                        null,
                        null,
                        "createdDate",
                        "desc"));
    }

    @Test
    void publicCollectionCanBeReadAnonymously() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        when(collectionDao.getById(1L))
                .thenReturn(
                        collection(
                                1L,
                                "alice",
                                true));

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        mock(MediaItemDao.class),
                        mock(UserDao.class));

        assertEquals(
                "Favorites",
                controller.get(
                        1L,
                        null)
                        .getName());
    }

    @Test
    void privateCollectionRespectsOwnership() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(collectionDao.getById(1L))
                .thenReturn(
                        collection(
                                1L,
                                "alice",
                                false));

        when(userDao.getRoles("bob"))
                .thenReturn(
                        List.of("USER"));

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        mock(MediaItemDao.class),
                        userDao);

        assertNotNull(
                controller.get(
                        1L,
                        principal("alice")));

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.get(
                                1L,
                                principal("bob")));
    }

    @Test
    void createUpdateDeleteCollection() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        MediaCollection existing =
                collection(
                        1L,
                        "alice",
                        false);

        when(collectionDao.getById(1L))
                .thenReturn(existing);

        when(collectionDao.create(
                any(MediaCollection.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        when(collectionDao.update(
                any(MediaCollection.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        itemDao,
                        userDao);

        MediaCollection created =
                controller.create(
                        collection(
                                0L,
                                "ignored",
                                true),
                        principal("alice"));

        assertEquals(
                "alice",
                created.getOwner());

        MediaCollection updated =
                controller.update(
                        1L,
                        collection(
                                1L,
                                "ignored",
                                true),
                        principal("alice"));

        assertEquals(
                "alice",
                updated.getOwner());

        controller.delete(
                1L,
                principal("alice"));

        verify(collectionDao)
                .delete(1L);
    }

    @Test
    void invalidCollectionFails() {
        MediaCollectionController controller =
                new MediaCollectionController(
                        mock(MediaCollectionDao.class),
                        mock(MediaItemDao.class),
                        mock(UserDao.class));

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.create(
                                new MediaCollection(),
                                principal("alice")));
    }

    @Test
    void collectionItemsCanBeViewedAddedAndRemoved() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        MediaCollection collection =
                collection(
                        1L,
                        "alice",
                        true);

        MediaItem item =
                new MediaItem();

        item.setId(5L);

        when(collectionDao.getById(1L))
                .thenReturn(collection);

        when(collectionDao.getItems(1L))
                .thenReturn(
                        List.of(item));

        when(itemDao.getById(5L))
                .thenReturn(item);

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        itemDao,
                        mock(UserDao.class));

        assertEquals(
                1,
                controller.items(
                        1L,
                        null)
                        .size());

        controller.addItem(
                1L,
                new AddItemToCollectionRequest(
                        5L),
                principal("alice"));

        controller.removeItem(
                1L,
                5L,
                principal("alice"));

        verify(collectionDao)
                .addItem(
                        1L,
                        5L);

        verify(collectionDao)
                .removeItem(
                        1L,
                        5L);
    }

    @Test
    void addingUnknownItemFails() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        when(collectionDao.getById(1L))
                .thenReturn(
                        collection(
                                1L,
                                "alice",
                                false));

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        mock(MediaItemDao.class),
                        mock(UserDao.class));

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.addItem(
                                1L,
                                new AddItemToCollectionRequest(),
                                principal("alice")));
    }

    @Test
    void missingCollectionFails() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        when(collectionDao.getById(99L))
                .thenReturn(null);

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        mock(MediaItemDao.class),
                        mock(UserDao.class));

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.get(
                                99L,
                                null));
    }

    @Test
    void adminCanDeleteOtherUsersCollection() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(collectionDao.getById(1L))
                .thenReturn(
                        collection(
                                1L,
                                "alice",
                                false));

        when(userDao.getRoles("admin"))
                .thenReturn(
                        List.of("ADMIN"));

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        mock(MediaItemDao.class),
                        userDao);

        controller.delete(
                1L,
                principal("admin"));

        verify(collectionDao)
                .delete(1L);
    }
}
