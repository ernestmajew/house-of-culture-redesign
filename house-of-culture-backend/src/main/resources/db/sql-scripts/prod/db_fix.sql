-- liquibase formatted sql
-- changeset kkielczy:db_fix

CREATE TABLE post_category (
     category_id bigint NOT NULL,
     post_id bigint NOT NULL,
     FOREIGN KEY (category_id) REFERENCES category (id),
     FOREIGN KEY (post_id) REFERENCES post (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE post_photo
DROP COLUMN photo_data;

ALTER TABLE institution_contact_info
    CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT;