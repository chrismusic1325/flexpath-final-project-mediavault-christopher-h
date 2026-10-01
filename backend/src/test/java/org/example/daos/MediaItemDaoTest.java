package org.example.daos;

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
class MediaItemDaoTest {
    private JdbcTemplate jdbc;
    private MediaItemDao dao;

    @BeforeEach
    void setup() {
        jdbc =
                mock(JdbcTemplate.class);

        dao =
                new MediaItemDao(
                        mock(DataSource.class));

        ReflectionTestUtils.setField(
                dao,
                "jdbcTemplate",
                jdbc);
    }

    @Test
    void getByIdReturnsItemOrNull() {
        MediaItem item =
                new MediaItem();

        item.setId(1L);

        when(jdbc.queryForObject(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(item)
                .thenThrow(
                        new EmptyResultDataAccessException(
                                1));

        assertSame(
                item,
                dao.getById(1L));

        assertNull(
                dao.getById(2L));
    }

    @Test
    void searchSupportsVisibilityFiltersAndSorting() {
        when(jdbc.query(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(List.of());

        assertNotNull(
                dao.search(
                        null,
                        false,
                        "title",
                        "creator",
                        "Music",
                        "title",
                        "asc"));

        assertNotNull(
                dao.search(
                        "alice",
                        false,
                        "",
                        "",
                        "",
                        "creator",
                        "desc"));

        assertNotNull(
                dao.search(
                        "admin",
                        true,
                        null,
                        null,
                        null,
                        "mediaType",
                        "asc"));

        assertNotNull(
                dao.search(
                        "admin",
                        true,
                        null,
                        null,
                        null,
                        "createdDate",
                        "desc"));
    }

    @Test
    void createUpdateDeleteDelegateToJdbc() {
        MediaItem item =
                new MediaItem();

        item.setId(1L);
        item.setTitle("Title");
        item.setCreator("Creator");
        item.setMediaType("Music");
        item.setDescription("Description");
        item.setPublic(true);
        item.setOwner("alice");

        when(jdbc.queryForObject(
                anyString(),
                any(RowMapper.class),
                any(Object[].class)))
                .thenReturn(item);

        assertSame(
                item,
                dao.create(item));

        assertSame(
                item,
                dao.update(item));

        dao.delete(1L);

        verify(
                jdbc,
                atLeast(3))
                .update(
                        anyString(),
                        any(Object[].class));
    }

    @Test
    void rowMapperMapsAllFields() throws Exception {
        ResultSet rs =
                mock(ResultSet.class);

        LocalDateTime now =
                LocalDateTime.now();

        when(rs.getLong("id"))
                .thenReturn(7L);

        when(rs.getString("title"))
                .thenReturn("Title");

        when(rs.getString("creator"))
                .thenReturn("Creator");

        when(rs.getString("media_type"))
                .thenReturn("Book");

        when(rs.getString("description"))
                .thenReturn("Description");

        when(rs.getBoolean("is_public"))
                .thenReturn(true);

        when(rs.getString("owner"))
                .thenReturn("alice");

        when(rs.getTimestamp("created_date"))
                .thenReturn(
                        Timestamp.valueOf(now));

        MediaItem item =
                ReflectionTestUtils.invokeMethod(
                        dao,
                        "map",
                        rs,
                        0);

        assertNotNull(item);
        assertEquals(7L, item.getId());
        assertEquals("Title", item.getTitle());
        assertTrue(item.isPublic());
    }
}
