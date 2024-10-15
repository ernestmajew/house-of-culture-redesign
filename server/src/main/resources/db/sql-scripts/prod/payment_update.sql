-- liquibase formatted sql
-- changeset ppuchala:payment_update

ALTER TABLE enrollment
    DROP FOREIGN KEY enrollment_ibfk_3;

ALTER TABLE enrollment
    DROP COLUMN payment_id;

ALTER TABLE old_enrollment
    DROP FOREIGN KEY old_enrollment_ibfk_3;

ALTER TABLE old_enrollment
    DROP COLUMN payment_id;

DROP TABLE payment;

CREATE TABLE payment
(
    id binary(16) NOT NULL PRIMARY KEY,
    payu_id VARCHAR(255) NULL,
    status VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    time   DATETIME NOT NULL,
    buyer_id  BIGINT NOT NULL,
    CONSTRAINT payment_ibfk_1
        FOREIGN KEY (buyer_id) REFERENCES user (id)
);

ALTER TABLE enrollment
    ADD COLUMN payment_id binary(16),
    ADD CONSTRAINT enrollment_payment_fk
        FOREIGN KEY (payment_id) REFERENCES payment (id);

ALTER TABLE old_enrollment
    ADD COLUMN payment_id binary(16),
    ADD CONSTRAINT old_enrollment_payment_fk
        FOREIGN KEY (payment_id) REFERENCES payment (id);
