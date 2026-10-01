package org.example.daos;

import org.example.models.MediaCollection;
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
public class MediaCollectionDao {
    private final JdbcTemplate jdbcTemplate;

    public MediaCollectionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public MediaCollection getById(long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM media_collections WHERE id = ?",
                    this::mapCollection,
                    id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<MediaCollection> search(
            String viewer,
            boolean admin,
            String name,
            String owner,
            String sortBy,
            String direction) {

        StringBuilder sql =
                new StringBuilder(
                        "SELECT * FROM media_collections WHERE 1=1");

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

        if (name != null && !name.isBlank()) {
            sql.append(
                    " AND LOWER(name) LIKE LOWER(?)");
            parameters.add("%" + name.trim() + "%");
        }

        if (owner != null && !owner.isBlank()) {
            sql.append(
                    " AND LOWER(owner) = LOWER(?)");
            parameters.add(owner.trim());
        }

        sql.append(" ORDER BY ")
                .append(sortColumn(sortBy))
                .append(" ")
                .append(sortDirection(direction));

        return jdbcTemplate.query(
                sql.toString(),
                this::mapCollection,
                parameters.toArray());
    }

    public MediaCollection create(
            MediaCollection collection) {

        jdbcTemplate.update(
                """
                INSERT INTO media_collections
                (name, description, is_public, owner)
                VALUES (?, ?, ?, ?)
                """,
                collection.getName(),
                collection.getDescription(),
                collection.isPublic(),
                collection.getOwner());

        return jdbcTemplate.queryForObject(
                """
                SELECT *
                FROM media_collections
                WHERE owner = ?
                ORDER BY id DESC
                LIMIT 1
                """,
                this::mapCollection,
                collection.getOwner());
    }

    public MediaCollection update(
            MediaCollection collection) {

        jdbcTemplate.update(
                """
                UPDATE media_collections
                SET name = ?,
                    description = ?,
                    is_public = ?
                WHERE id = ?
                """,
                collection.getName(),
                collection.getDescription(),
                collection.isPublic(),
                collection.getId());

        return getById(collection.getId());
    }

    public int delete(long id) {
        return jdbcTemplate.update(
                "DELETE FROM media_collections WHERE id = ?",
                id);
    }

    public void addItem(long collectionId, long itemId) {
        jdbcTemplate.update(
                """
                INSERT IGNORE INTO collection_items
                (collection_id, item_id)
                VALUES (?, ?)
                """,
                collectionId,
                itemId);
    }

    public int removeItem(
            long collectionId,
            long itemId) {

        return jdbcTemplate.update(
                """
                DELETE FROM collection_items
                WHERE collection_id = ?
                  AND item_id = ?
                """,
                collectionId,
                itemId);
    }

    public List<MediaItem> getItems(long collectionId) {
        return jdbcTemplate.query(
                """
                SELECT mi.*
                FROM media_items mi
                INNER JOIN collection_items ci
                    ON ci.item_id = mi.id
                WHERE ci.collection_id = ?
                ORDER BY mi.title
                """,
                this::mapItem,
                collectionId);
    }

    private String sortColumn(String sortBy) {
        if ("name".equals(sortBy)) {
            return "name";
        }
        if ("owner".equals(sortBy)) {
            return "owner";
        }
        return "created_date";
    }

    private String sortDirection(String direction) {
        return "asc".equalsIgnoreCase(direction)
                ? "ASC"
                : "DESC";
    }

    private MediaCollection mapCollection(
            ResultSet resultSet,
            int row) throws SQLException {

        MediaCollection collection =
                new MediaCollection();

        collection.setId(resultSet.getLong("id"));
        collection.setName(resultSet.getString("name"));
        collection.setDescription(
                resultSet.getString("description"));
        collection.setPublic(
                resultSet.getBoolean("is_public"));
        collection.setOwner(
                resultSet.getString("owner"));

        if (resultSet.getTimestamp("created_date") != null) {
            collection.setCreatedDate(
                    resultSet.getTimestamp("created_date")
                            .toLocalDateTime());
        }

        return collection;
    }

    private MediaItem mapItem(
            ResultSet resultSet,
            int row) throws SQLException {

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
