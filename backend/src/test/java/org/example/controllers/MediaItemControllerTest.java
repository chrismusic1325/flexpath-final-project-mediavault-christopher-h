package org.example.controllers;

import org.example.daos.MediaItemDao;
import org.example.daos.UserDao;
import org.example.models.MediaItem;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MediaItemControllerTest {
    private Principal principal(String name) {
        return () -> name;
    }

    private MediaItem item(
            long id,
            String owner,
            boolean isPublic) {

        MediaItem item =
                new MediaItem();

        item.setId(id);
        item.setTitle("Title");
        item.setCreator("Creator");
        item.setMediaType("Music");
        item.setDescription("Description");
        item.setPublic(isPublic);
        item.setOwner(owner);

        return item;
    }

    @Test
    void publicAndAuthenticatedSearchDelegate() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.search(
                any(),
                anyBoolean(),
                any(),
                any(),
                any(),
                any(),
                any()))
                .thenReturn(List.of());

        when(userDao.getRoles("admin"))
                .thenReturn(
                        List.of("ADMIN"));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        assertNotNull(
                controller.publicItems(
                        "song",
                        "creator",
                        "Music",
                        "title",
                        "asc"));

        assertNotNull(
                controller.items(
                        principal("admin"),
                        null,
                        null,
                        null,
                        "createdDate",
                        "desc"));
    }

    @Test
    void publicItemCanBeReadAnonymously() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.getById(1L))
                .thenReturn(
                        item(
                                1L,
                                "alice",
                                true));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        assertEquals(
                1L,
                controller.get(
                        1L,
                        null)
                        .getId());
    }

    @Test
    void ownerAndAdminCanReadPrivateItem() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.getById(1L))
                .thenReturn(
                        item(
                                1L,
                                "alice",
                                false));

        when(userDao.getRoles("admin"))
                .thenReturn(
                        List.of("ADMIN"));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        assertNotNull(
                controller.get(
                        1L,
                        principal("alice")));

        assertNotNull(
                controller.get(
                        1L,
                        principal("admin")));
    }

    @Test
    void strangerCannotReadPrivateItem() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.getById(1L))
                .thenReturn(
                        item(
                                1L,
                                "alice",
                                false));

        when(userDao.getRoles("bob"))
                .thenReturn(
                        List.of("USER"));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.get(
                                1L,
                                principal("bob")));
    }

    @Test
    void missingItemReturnsNotFound() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.getById(99L))
                .thenReturn(null);

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.get(
                                99L,
                                null));
    }

    @Test
    void createSetsOwnerAndDefaultCreator() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.create(
                any(MediaItem.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        MediaItem item =
                new MediaItem();

        item.setTitle("Song");
        item.setMediaType("Music");

        MediaItem created =
                controller.create(
                        item,
                        principal("alice"));

        assertEquals(
                "alice",
                created.getOwner());

        assertEquals(
                "alice",
                created.getCreator());
    }

    @Test
    void invalidCreateFails() {
        MediaItemController controller =
                new MediaItemController(
                        mock(MediaItemDao.class),
                        mock(UserDao.class));

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.create(
                                new MediaItem(),
                                principal("alice")));
    }

    @Test
    void ownerCanUpdateAndDelete() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        MediaItem existing =
                item(
                        1L,
                        "alice",
                        false);

        when(itemDao.getById(1L))
                .thenReturn(existing);

        when(itemDao.update(
                any(MediaItem.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        MediaItem update =
                item(
                        1L,
                        "ignored",
                        true);

        MediaItem result =
                controller.update(
                        1L,
                        update,
                        principal("alice"));

        assertEquals(
                "alice",
                result.getOwner());

        controller.delete(
                1L,
                principal("alice"));

        verify(itemDao).delete(1L);
    }

    @Test
    void adminCanDeleteAnotherUsersItem() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.getById(1L))
                .thenReturn(
                        item(
                                1L,
                                "alice",
                                false));

        when(userDao.getRoles("admin"))
                .thenReturn(
                        List.of("ADMIN"));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        controller.delete(
                1L,
                principal("admin"));

        verify(itemDao).delete(1L);
    }

    @Test
    void strangerCannotDelete() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.getById(1L))
                .thenReturn(
                        item(
                                1L,
                                "alice",
                                false));

        when(userDao.getRoles("bob"))
                .thenReturn(
                        List.of("USER"));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        assertThrows(
                ResponseStatusException.class,
                () ->
                        controller.delete(
                                1L,
                                principal("bob")));

        verify(itemDao, never())
                .delete(1L);
    }
}
