-- liquibase formatted sql
-- changeset ppuchal:social_media_tokens_init
-- comment Update keeping social media tokens in db

DROP TABLE IF EXISTS social_media_tokens;

CREATE TABLE facebook_api_data
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    integration_status   VARCHAR(255)                      NOT NULL,
    user_access_token    TEXT,
    username             VARCHAR(255),
    page_access_token    TEXT,
    page_id              VARCHAR(255),
    page_name            VARCHAR(255),
    instagram_profile_id VARCHAR(255),
    instagram_username   VARCHAR(255),
    expiration_time      DATETIME
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
