-- liquibase formatted sql
-- changeset ppuchala:add geolocation contact info

ALTER TABLE institution_contact_info
    ADD latitude DOUBLE DEFAULT NULL,
    ADD longitude DOUBLE DEFAULT NULL,
    ADD institution_name VARCHAR(255) NOT NULL,
    MODIFY COLUMN email VARCHAR(255) DEFAULT NULL,
    MODIFY COLUMN phone_number VARCHAR(255) DEFAULT NULL,
    MODIFY COLUMN address_first_line VARCHAR(255) DEFAULT NULL,
    MODIFY COLUMN address_second_line VARCHAR(255) DEFAULT NULL,
    MODIFY COLUMN facebook_url VARCHAR(255) DEFAULT NULL,
    MODIFY COLUMN instagram_url VARCHAR(255) DEFAULT NULL;
