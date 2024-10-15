-- liquibase formatted sql
-- changeset ppuchala:update_event_table

ALTER TABLE event MODIFY instructor_id BIGINT(20) DEFAULT NULL;
ALTER TABLE event ADD COLUMN type TINYINT NOT NULL;
ALTER TABLE event MODIFY status TINYINT NOT NULL;
ALTER TABLE event MODIFY description TEXT NOT NULL;

ALTER TABLE single_event MODIFY starts DATETIME NOT NULL;
ALTER TABLE single_event DROP COLUMN duration;
ALTER TABLE single_event ADD COLUMN ends DATETIME NOT NULL;
ALTER TABLE single_event MODIFY is_cancelled BOOLEAN NOT NULL;

CREATE TABLE event_category (
    event_id BIGINT(20) NOT NULL,
    category_id BIGINT(20) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);