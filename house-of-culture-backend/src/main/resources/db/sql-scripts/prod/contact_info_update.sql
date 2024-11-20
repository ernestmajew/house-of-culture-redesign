-- liquibase formatted sql
-- changeset ppuchal:contact_info_update
-- comment Contact info update

ALTER TABLE institution_contact_info
    ADD COLUMN id                  INTEGER      NOT NULL,
    ADD COLUMN address_first_line  VARCHAR(255) NOT NULL,
    ADD COLUMN address_second_line VARCHAR(255) NOT NULL,
    CHANGE email email VARCHAR(255) NOT NULL,
    CHANGE phone_number phone_number VARCHAR(255) NOT NULL,
    ADD COLUMN facebook_url        VARCHAR(255) DEFAULT NULL,
    ADD COLUMN instagram_url       VARCHAR(255) DEFAULT NULL;

ALTER TABLE institution_contact_info
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (id);

ALTER TABLE institution_contact_info
    DROP COLUMN address;