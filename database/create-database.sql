CREATE DATABASE IF NOT EXISTS flexpath_final;
USE flexpath_final;

DROP TABLE IF EXISTS collection_items;
DROP TABLE IF EXISTS media_collections;
DROP TABLE IF EXISTS media_items;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
    username VARCHAR(255) NOT NULL,
    role VARCHAR(250) NOT NULL,
    PRIMARY KEY (username, role),
    CONSTRAINT fk_roles_user
        FOREIGN KEY (username)
        REFERENCES users(username)
        ON DELETE CASCADE
);

CREATE TABLE media_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    creator VARCHAR(255),
    media_type VARCHAR(100) NOT NULL,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    owner VARCHAR(255) NOT NULL,
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_media_item_owner
        FOREIGN KEY (owner)
        REFERENCES users(username)
        ON DELETE CASCADE
);

CREATE TABLE media_collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    owner VARCHAR(255) NOT NULL,
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_collection_owner
        FOREIGN KEY (owner)
        REFERENCES users(username)
        ON DELETE CASCADE
);

CREATE TABLE collection_items (
    collection_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    PRIMARY KEY (collection_id, item_id),
    CONSTRAINT fk_collection_items_collection
        FOREIGN KEY (collection_id)
        REFERENCES media_collections(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_collection_items_item
        FOREIGN KEY (item_id)
        REFERENCES media_items(id)
        ON DELETE CASCADE
);

INSERT INTO users (username, password) VALUES
('admin', '$2a$10$tBTfzHzjmQVKza3VSa5lsOX6/iL93xPVLlLXYg2FhT6a.jb1o6VDq'),
('user', '$2a$10$tBTfzHzjmQVKza3VSa5lsOX6/iL93xPVLlLXYg2FhT6a.jb1o6VDq');

INSERT INTO roles (username, role) VALUES
('admin', 'ADMIN'),
('user', 'USER');

INSERT INTO media_items
(title, creator, media_type, description, is_public, owner)
VALUES
('Welcome to MediaVault', 'MediaVault', 'Article',
 'A starter public media item.', TRUE, 'admin'),
('Private Favorites', 'MediaVault User', 'Note',
 'Example private media item.', FALSE, 'user');

INSERT INTO media_collections
(name, description, is_public, owner)
VALUES
('Public Library', 'A starter public MediaVault collection.', TRUE, 'admin'),
('My Private Collection', 'A starter private collection.', FALSE, 'user');

INSERT INTO collection_items (collection_id, item_id)
VALUES (1, 1);
