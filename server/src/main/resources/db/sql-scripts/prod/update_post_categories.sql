-- liquibase formatted sql
-- changeset ppuchal:update_post_categories
-- comment Update posts categories

ALTER TABLE category
    RENAME COLUMN value to name;

ALTER TABLE post
    DROP FOREIGN KEY post_ibfk_1;

DROP INDEX event_id ON post;

ALTER TABLE post
    DROP COLUMN event_id;