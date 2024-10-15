-- liquibase formatted sql
-- changeset ppuchal:update_user_nullable_fields
-- comment Update posts categories

ALTER TABLE user
    MODIFY COLUMN user_type varchar(255) NOT NULL,
    MODIFY COLUMN birthdate date NOT NULL,
    MODIFY COLUMN gets_newsletter tinyint(1) NOT NULL;
