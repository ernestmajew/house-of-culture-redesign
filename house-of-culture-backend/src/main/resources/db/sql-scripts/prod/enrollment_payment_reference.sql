-- liquibase formatted sql
-- changeset ppuchal:enrollment_payment_reference
-- comment Add reference to payment in enrollment/old enrollment table

ALTER TABLE enrollment
    ADD COLUMN payment_id BIGINT(20) DEFAULT NULL,
    ADD CONSTRAINT enrollment_ibfk_3 FOREIGN KEY (payment_id) REFERENCES payment(id);

ALTER TABLE old_enrollment
    ADD COLUMN payment_id BIGINT(20) DEFAULT NULL,
    ADD CONSTRAINT old_enrollment_ibfk_3 FOREIGN KEY (payment_id) REFERENCES payment(id);

# Drop column on payment
ALTER TABLE payment
    DROP FOREIGN KEY payment_ibfk_1;
DROP INDEX enrollment_id ON payment;
ALTER TABLE payment
    DROP COLUMN enrollment_id;

ALTER TABLE single_event
    DROP COLUMN free_places;