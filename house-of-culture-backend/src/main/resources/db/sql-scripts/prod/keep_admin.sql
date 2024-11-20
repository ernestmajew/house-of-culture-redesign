-- liquibase formatted sql
-- changeset kkielczy:keep_admin_account


UPDATE `user` SET `user_type` = 'CLIENT' WHERE (`email` = 'orzeldev@gmail.com');
