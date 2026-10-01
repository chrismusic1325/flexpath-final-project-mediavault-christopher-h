package org.example.controllers;

import org.example.daos.MediaCollectionDao;
import org.example.daos.MediaItemDao;
import org.example.daos.UserDao;
import org.example.models.MediaCollection;
import org.junit.jupiter.api.Test;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MediaCollectionControllerTest {
    @Test
    void createSetsOwner() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        when(collectionDao.create(
                any(MediaCollection.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0));

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        itemDao,
                        userDao);

        MediaCollection collection =
                new MediaCollection();

        collection.setName(
                "Favorites");

        Principal principal =
                () -> "user";

        MediaCollection created =
                controller.create(
                        collection,
                        principal);

        assertEquals(
                "user",
                created.getOwner());
    }

    @Test
    void publicCollectionIsReadableWithoutLogin() {
        MediaCollectionDao collectionDao =
                mock(MediaCollectionDao.class);

        MediaItemDao itemDao =
                mock(MediaItemDao.class);

        UserDao userDao =
                mock(UserDao.class);

        MediaCollection collection =
                new MediaCollection();

        collection.setId(1L);
        collection.setName("Public");
        collection.setPublic(true);
        collection.setOwner("user");

        when(collectionDao.getById(1L))
                .thenReturn(collection);

        MediaCollectionController controller =
                new MediaCollectionController(
                        collectionDao,
                        itemDao,
                        userDao);

        assertEquals(
                "Public",
                controller.get(
                        1L,
                        null)
                        .getName());
    }
}
