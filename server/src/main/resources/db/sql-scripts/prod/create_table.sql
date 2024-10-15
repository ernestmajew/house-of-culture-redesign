-- liquibase formatted sql
-- changeset mgabka:create_table
-- comment Create tables

UPDATE DATABASECHANGELOGLOCK SET `LOCKED` = 1, LOCKEDBY = NULL, LOCKGRANTED = NOW() WHERE ID = 1 AND `LOCKED` = 0;

DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    id              BIGINT(20)   NOT NULL AUTO_INCREMENT,
    login           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    salt            VARCHAR(255) NOT NULL,
    role            VARCHAR(255) NOT NULL,
    status          VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    phone_number    VARCHAR(255) DEFAULT NULL,
    first_name      VARCHAR(255) NOT NULL,
    last_name       VARCHAR(255) NOT NULL,
    birthdate       DATE         DEFAULT NULL,
    gets_newsletter BOOLEAN      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS category;

CREATE TABLE category
(
    id    BIGINT(20)   NOT NULL AUTO_INCREMENT,
    value VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS event;

CREATE TABLE event
(
    id            BIGINT(20)   NOT NULL AUTO_INCREMENT,
    title         VARCHAR(255) NOT NULL,
    description   VARCHAR(255) DEFAULT NULL,
    minimum_age   INT          DEFAULT NULL,
    maximum_age   INT          DEFAULT NULL,
    max_places    INT          DEFAULT NULL,
    instructor_id BIGINT(20)   NOT NULL,
    cost          DOUBLE       DEFAULT NULL,
    status        VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (instructor_id) REFERENCES user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS event_photo;

CREATE TABLE event_photo
(
    id         BIGINT(20)   NOT NULL AUTO_INCREMENT,
    event_id   BIGINT(20)   NOT NULL,
    photo_data BLOB         NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (event_id) REFERENCES event (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS single_event;

CREATE TABLE single_event
(
    id           BIGINT(20) NOT NULL AUTO_INCREMENT,
    event_id     BIGINT(20) NOT NULL,
    starts       DATETIME   DEFAULT NULL,
    duration     INT        DEFAULT NULL,
    free_places  INT        DEFAULT NULL,
    is_cancelled BOOLEAN    DEFAULT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (event_id) REFERENCES event (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS enrollment;

CREATE TABLE enrollment
(
    id              BIGINT(20) NOT NULL AUTO_INCREMENT,
    client_id       BIGINT(20) NOT NULL,
    event_id        BIGINT(20) NOT NULL,
    enrollment_time DATETIME   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (client_id) REFERENCES user (id),
    FOREIGN KEY (event_id)  REFERENCES single_event (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS payment;

CREATE TABLE payment
(
    id            BIGINT(20)   NOT NULL AUTO_INCREMENT,
    status        VARCHAR(255) NOT NULL,
    amount        DOUBLE       NOT NULL,
    time          DATETIME     NOT NULL,
    method        VARCHAR(255) DEFAULT NULL,
    enrollment_id BIGINT(20)   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (enrollment_id) REFERENCES enrollment (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS old_enrollment;

CREATE TABLE old_enrollment
(
    id              BIGINT(20) NOT NULL AUTO_INCREMENT,
    client_id       BIGINT(20) NOT NULL,
    event_id        BIGINT(20) NOT NULL,
    enrollment_time DATETIME   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (client_id) REFERENCES user (id),
    FOREIGN KEY (event_id)  REFERENCES single_event (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS post;

CREATE TABLE post
(
    id          BIGINT(20)   NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    created_at  DATETIME     NOT NULL,
    fb_post_id  VARCHAR(255) DEFAULT NULL,
    ig_post_id  VARCHAR(255) DEFAULT NULL,
    event_id    BIGINT(20)   NOT NULL,
    author_id   BIGINT(20)   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id)  REFERENCES event (id),
    FOREIGN KEY (author_id) REFERENCES user (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS post_photo;

CREATE TABLE post_photo
(
    id         BIGINT(20)   NOT NULL AUTO_INCREMENT,
    post_id    BIGINT(20)   NOT NULL,
    photo_data BLOB         NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (post_id) REFERENCES post (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS notification;

CREATE TABLE notification
(
    id          BIGINT(20)   NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255) NOT NULL,
    content     VARCHAR(255) DEFAULT NULL,
    status      VARCHAR(255) NOT NULL,
    sent_time   DATETIME     NOT NULL,
    event_id    BIGINT(20)   NOT NULL,
    author_id   BIGINT(20)   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id)  REFERENCES event (id),
    FOREIGN KEY (author_id) REFERENCES user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS notification_assignment;

CREATE TABLE notification_assignment
(
    id                BIGINT(20)   NOT NULL AUTO_INCREMENT,
    notification_id   BIGINT(20)   NOT NULL,
    assigment_data    BLOB         NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (notification_id) REFERENCES notification (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS institution_contact_info;

CREATE TABLE institution_contact_info
(
    address      VARCHAR(255) NOT NULL,
    email        VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (address)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS social_media_tokens;

CREATE TABLE social_media_tokens
(
    token         VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    valid_until   DATETIME     NOT NULL,
    platform      VARCHAR(255) NOT NULL,
    PRIMARY KEY (token)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

UPDATE DATABASECHANGELOGLOCK SET `LOCKED` = 0, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;