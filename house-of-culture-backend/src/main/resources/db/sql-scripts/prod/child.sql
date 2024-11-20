-- liquibase formatted sql
-- changeset kkielczy:add child

ALTER TABLE `user`
    ADD COLUMN parent_id BIGINT(20) NULL,
    ADD COLUMN user_type VARCHAR(255) NULL,
    ADD FOREIGN KEY (parent_id) REFERENCES user (id);