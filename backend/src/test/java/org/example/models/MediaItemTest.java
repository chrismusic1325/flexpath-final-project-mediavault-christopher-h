package org.example.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaItemTest {
    @Test
    void storesMediaItemFields() {
        MediaItem item = new MediaItem();

        item.setId(7L);
        item.setTitle("Test Item");
        item.setCreator("Creator");
        item.setMediaType("Music");
        item.setDescription("Description");
        item.setPublic(true);
        item.setOwner("user");

        assertEquals(7L, item.getId());
        assertEquals("Test Item", item.getTitle());
        assertEquals("Creator", item.getCreator());
        assertEquals("Music", item.getMediaType());
        assertEquals("Description", item.getDescription());
        assertTrue(item.isPublic());
        assertEquals("user", item.getOwner());
    }
}
