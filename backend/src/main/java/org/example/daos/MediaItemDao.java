package org.example.daos;

import org.example.models.MediaItem;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MediaItemDao {
    private final JdbcTemplate jdbcTemplate;

    public MediaItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public MediaItem getById(long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM media_items WHERE id = ?",
                    this::map,
                    id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<MediaItem> search(
            String viewer,
            boolean admin,
            String title,
            String creator,
            String mediaType,
            String sortBy,
            String direction) {

        StringBuilder sql =
                new StringBuilder(
                        "SELECT * FROM media_items WHERE 1=1");

        List<Object> parameters = new ArrayList<>();

        if (!admin) {
            if (viewer == null || viewer.isBlank()) {
                sql.append(" AND is_public = TRUE");
            } else {
                sql.append(
                        " AND (is_public = TRUE OR owner = ?)");
                parameters.add(viewer);
            }
        }

        if (title != null && !title.isBlank()) {
            sql.append(
                    " AND LOWER(title) LIKE LOWER(?)");
            parameters.add("%" + title.trim() + "%");
        }

        if (creator != null && !creator.isBlank()) {
            sql.append(
                    " AND LOWER(creator) LIKE LOWER(?)");
            parameters.add("%" + creator.trim() + "%");
        }

        if (mediaType != null && !mediaType.isBlank()) {
            sql.append(
                    " AND LOWER(media_type) = LOWER(?)");
            parameters.add(mediaType.trim());
        }

        sql.append(" ORDER BY ")
                .append(sortColumn(sortBy))
                .append(" ")
                .append(sortDirection(direction));

        return jdbcTemplate.query(
                sql.toString(),
                this::map,
                parameters.toArray());
    }

    public MediaItem create(MediaItem item) {
        jdbcTemplate.update(
                """
                INSERT INTO media_items
                (title, creator, media_type, description, is_public, owner)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                item.getTitle(),
                item.getCreator(),
                item.getMediaType(),
                item.getDescription(),
                item.isPublic(),
                item.getOwner());

        return jdbcTemplate.queryForObject(
                """
                SELECT *
                FROM media_items
                WHERE owner = ?
                ORDER BY id DESC
                LIMIT 1
                """,
                this::map,
                item.getOwner());
    }

    public MediaItem update(MediaItem item) {
        jdbcTemplate.update(
                """
                UPDATE media_items
                SET title = ?,
                    creator = ?,
                    media_type = ?,
                    description = ?,
                    is_public = ?
                WHERE id = ?
                """,
                item.getTitle(),
                item.getCreator(),
                item.getMediaType(),
                item.getDescription(),
                item.isPublic(),
                item.getId());

        return getById(item.getId());
    }

    public int delete(long id) {
        return jdbcTemplate.update(
                "DELETE FROM media_items WHERE id = ?",
                id);
    }

    private String sortColumn(String sortBy) {
        if ("title".equals(sortBy)) {
            return "title";
        }
        if ("creator".equals(sortBy)) {
            return "creator";
        }
        if ("mediaType".equals(sortBy)) {
            return "media_type";
        }
        return "created_date";
    }

    private String sortDirection(String direction) {
        return "asc".equalsIgnoreCase(direction)
                ? "ASC"
                : "DESC";
    }

    private MediaItem map(ResultSet resultSet, int row)
            throws SQLException {

        MediaItem item = new MediaItem();

        item.setId(resultSet.getLong("id"));
        item.setTitle(resultSet.getString("title"));
        item.setCreator(resultSet.getString("creator"));
        item.setMediaType(resultSet.getString("media_type"));
        item.setDescription(resultSet.getString("description"));
        item.setPublic(resultSet.getBoolean("is_public"));
        item.setOwner(resultSet.getString("owner"));

        if (resultSet.getTimestamp("created_date") != null) {
            item.setCreatedDate(
                    resultSet.getTimestamp("created_date")
                            .toLocalDateTime());
        }

        return item;
    }
}
