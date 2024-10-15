-- liquibase formatted sql
-- changeset mgabka:update_post_description
-- comment Update posts description to update type of description into LONGTEXT in post table

ALTER TABLE post ADD COLUMN description_temp LONGTEXT DEFAULT NULL;

UPDATE post SET description_temp = description;

ALTER TABLE post DROP COLUMN description;

ALTER TABLE post RENAME COLUMN description_temp TO description;