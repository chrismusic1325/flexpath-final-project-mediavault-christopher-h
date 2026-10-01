package org.example.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelCoverageTest {
    @Test
    void mediaItemConstructorAndAccessors() {
        LocalDateTime now =
                LocalDateTime.now();

        MediaItem item =
                new MediaItem(
                        1L,
                        "Title",
                        "Creator",
                        "Music",
                        "Description",
                        true,
                        "owner",
                        now);

        assertEquals(1L, item.getId());
        assertEquals("Title", item.getTitle());
        assertEquals("Creator", item.getCreator());
        assertEquals("Music", item.getMediaType());
        assertEquals("Description", item.getDescription());
        assertTrue(item.isPublic());
        assertEquals("owner", item.getOwner());
        assertEquals(now, item.getCreatedDate());

        item.setId(2L);
        item.setTitle("New");
        item.setCreator("Other");
        item.setMediaType("Book");
        item.setDescription("Changed");
        item.setPublic(false);
        item.setOwner("user");
        item.setCreatedDate(now.plusDays(1));

        assertEquals(2L, item.getId());
        assertEquals("New", item.getTitle());
        assertFalse(item.isPublic());
    }

    @Test
    void collectionConstructorAndAccessors() {
        LocalDateTime now =
                LocalDateTime.now();

        MediaCollection collection =
                new MediaCollection(
                        3L,
                        "Favorites",
                        "Description",
                        false,
                        "user",
                        now);

        assertEquals(3L, collection.getId());
        assertEquals("Favorites", collection.getName());
        assertEquals("Description", collection.getDescription());
        assertFalse(collection.isPublic());
        assertEquals("user", collection.getOwner());
        assertEquals(now, collection.getCreatedDate());

        collection.setId(4L);
        collection.setName("Public");
        collection.setDescription("Changed");
        collection.setPublic(true);
        collection.setOwner("admin");
        collection.setCreatedDate(now.plusDays(1));

        assertEquals(4L, collection.getId());
        assertEquals("Public", collection.getName());
        assertTrue(collection.isPublic());
    }

    @Test
    void userAndDtoAccessors() {
        User user =
                new User("alice", "secret");

        assertEquals("alice", user.getUsername());
        assertEquals("secret", user.getPassword());

        user.setUsername("bob");
        user.setPassword("new");

        assertEquals("bob", user.getUsername());
        assertEquals("new", user.getPassword());

        User emptyUser = new User();
        emptyUser.setUsername("new-user");
        assertEquals("new-user", emptyUser.getUsername());

        UserDto dto =
                new UserDto(
                        "alice",
                        List.of("USER"));

        assertEquals("alice", dto.getUsername());
        assertEquals(List.of("USER"), dto.getRoles());

        UserDto emptyDto = new UserDto();
        emptyDto.setUsername("admin");
        emptyDto.setRoles(
                List.of("ADMIN"));

        assertEquals("admin", emptyDto.getUsername());
        assertEquals(List.of("ADMIN"), emptyDto.getRoles());
    }

    @Test
    void addItemRequestAccessors() {
        AddItemToCollectionRequest request =
                new AddItemToCollectionRequest(9L);

        assertEquals(9L, request.getItemId());

        AddItemToCollectionRequest empty =
                new AddItemToCollectionRequest();

        empty.setItemId(10L);

        assertEquals(10L, empty.getItemId());
    }
}
