-- liquibase formatted sql
-- changeset ppuchala:event_enum_types

ALTER TABLE event
    MODIFY COLUMN status VARCHAR(50) NOT NULL,
    MODIFY COLUMN type VARCHAR(50) NOT NULL;
