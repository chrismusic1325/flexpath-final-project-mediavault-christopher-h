package org.example.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaCollectionTest {
    @Test
    void storesCollectionFields() {
        MediaCollection collection =
                new MediaCollection();

        collection.setId(3L);
        collection.setName("Favorites");
        collection.setDescription(
                "My favorites");
        collection.setPublic(false);
        collection.setOwner("user");

        assertEquals(
                3L,
                collection.getId());

        assertEquals(
                "Favorites",
                collection.getName());

        assertEquals(
                "My favorites",
                collection.getDescription());

        assertFalse(
                collection.isPublic());

        assertEquals(
                "user",
                collection.getOwner());
    }
}
