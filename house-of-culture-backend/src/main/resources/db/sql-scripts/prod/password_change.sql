-- liquibase formatted sql
-- changeset kkielczy:password_change

CREATE TABLE password_change
(
    id binary(16) NOT NULL,
    code VARCHAR(255) NOT NULL,
    request_time datetime NOT NULL,
    expiration_time datetime NOT NULL,
    account_id bigint NOT NULL,
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`account_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;