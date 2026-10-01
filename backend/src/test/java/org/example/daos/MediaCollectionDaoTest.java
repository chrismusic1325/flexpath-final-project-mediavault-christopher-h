package org.example.daos;

import org.example.models.MediaCollection;
import org.example.models.MediaItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
class MediaCollectionDaoTest {
    private JdbcTemplate jdbc;
    private MediaCollectionDao dao;

    @BeforeEach
    void setup() {
        jdbc =
                mock(JdbcTemplate.class);

        dao =
                new MediaCollectionDao(
                        mock(DataSource.class));

        ReflectionTestUtils.setField(
                dao,
                "jdbcTemplate",
                jdbc);
    }

    @Test
    void getByIdReturnsCollectionOrNull() {
        MediaCollection collection =
                new MediaCollection();

        collection.setId(1L);

        when(jdbc.queryForObject(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(collection)
                .thenThrow(
                        new EmptyResultDataAccessException(
                                1));

        assertSame(
                collection,
                dao.getById(1L));

        assertNull(
                dao.getById(2L));
    }

    @Test
    void searchSupportsVisibilityAndSorting() {
        when(jdbc.query(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(List.of());

        assertNotNull(
                dao.search(
                        null,
                        false,
                        "fav",
                        "alice",
                        "name",
                        "asc"));

        assertNotNull(
                dao.search(
                        "alice",
                        false,
                        "",
                        "",
                        "owner",
                        "desc"));

        assertNotNull(
                dao.search(
                        "admin",
                        true,
                        null,
                        null,
                        "createdDate",
                        "desc"));
    }

    @Test
    void crudAndMembershipDelegateToJdbc() {
        MediaCollection collection =
                new MediaCollection();

        collection.setId(1L);
        collection.setName("Favorites");
        collection.setDescription("Description");
        collection.setPublic(true);
        collection.setOwner("alice");

        when(jdbc.queryForObject(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(collection);

        when(jdbc.query(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(List.of());

        assertSame(
                collection,
                dao.create(collection));

        assertSame(
                collection,
                dao.update(collection));

        dao.addItem(1L, 2L);
        dao.removeItem(1L, 2L);
        dao.delete(1L);

        assertNotNull(
                dao.getItems(1L));

        verify(
                jdbc,
                atLeast(5))
                .update(
                        anyString(),
                        any(Object[].class));
    }

    @Test
    void collectionRowMapperMapsFields() throws Exception {
        ResultSet rs =
                mock(ResultSet.class);

        LocalDateTime now =
                LocalDateTime.now();

        when(rs.getLong("id"))
                .thenReturn(4L);

        when(rs.getString("name"))
                .thenReturn("Favorites");

        when(rs.getString("description"))
                .thenReturn("Description");

        when(rs.getBoolean("is_public"))
                .thenReturn(true);

        when(rs.getString("owner"))
                .thenReturn("alice");

        when(rs.getTimestamp("created_date"))
                .thenReturn(
                        Timestamp.valueOf(now));

        MediaCollection collection =
                ReflectionTestUtils.invokeMethod(
                        dao,
                        "mapCollection",
                        rs,
                        0);

        assertNotNull(collection);
        assertEquals(4L, collection.getId());
        assertEquals("Favorites", collection.getName());
        assertTrue(collection.isPublic());
    }

    @Test
    void itemRowMapperMapsFields() throws Exception {
        ResultSet rs =
                mock(ResultSet.class);

        when(rs.getLong("id"))
                .thenReturn(9L);

        when(rs.getString("title"))
                .thenReturn("Item");

        when(rs.getString("creator"))
                .thenReturn("Creator");

        when(rs.getString("media_type"))
                .thenReturn("Music");

        when(rs.getString("description"))
                .thenReturn("Description");

        when(rs.getBoolean("is_public"))
                .thenReturn(false);

        when(rs.getString("owner"))
                .thenReturn("alice");

        when(rs.getTimestamp("created_date"))
                .thenReturn(null);

        MediaItem item =
                ReflectionTestUtils.invokeMethod(
                        dao,
                        "mapItem",
                        rs,
                        0);

        assertNotNull(item);
        assertEquals(9L, item.getId());
        assertFalse(item.isPublic());
    }
}
