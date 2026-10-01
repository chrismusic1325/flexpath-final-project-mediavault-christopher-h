package org.example.controllers;

import org.example.daos.MediaItemDao;
import org.example.daos.UserDao;
import org.example.models.MediaItem;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MediaItemControllerTest {
    @Test
    void publicSearchDelegatesToDao() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(itemDao.search(
                null,
                false,
                "song",
                null,
                null,
                "title",
                "asc"))
                .thenReturn(List.of());

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        assertNotNull(
                controller.publicItems(
                        "song",
                        null,
                        null,
                        "title",
                        "asc"));

        verify(itemDao).search(
                null,
                false,
                "song",
                null,
                null,
                "title",
                "asc");
    }

    @Test
    void createSetsOwnerFromPrincipal() {
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

        Principal principal =
                () -> "user";

        MediaItem created =
                controller.create(
                        item,
                        principal);

        assertEquals(
                "user",
                created.getOwner());

        assertEquals(
                "user",
                created.getCreator());
    }

    @Test
    void nonOwnerCannotDeletePrivateItem() {
        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        MediaItem existing =
                new MediaItem();

        existing.setId(1L);
        existing.setOwner("owner");

        when(itemDao.getById(1L))
                .thenReturn(existing);

        when(userDao.getRoles("other"))
                .thenReturn(List.of("USER"));

        MediaItemController controller =
                new MediaItemController(
                        itemDao,
                        userDao);

        Principal principal =
                () -> "other";

        assertThrows(
                ResponseStatusException.class,
                () -> controller.delete(
                        1L,
                        principal));

        verify(itemDao, never())
                .delete(1L);
    }
}
