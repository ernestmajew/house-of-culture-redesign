-- liquibase formatted sql
-- changeset kkielczy:drop_user_fields
-- comment Drop user fields

ALTER TABLE user
DROP COLUMN login,
DROP COLUMN salt;