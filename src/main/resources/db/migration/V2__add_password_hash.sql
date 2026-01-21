alter table user_account add column password_hash varchar(255);

update user_account
set password_hash = '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5L9FZ5ob9zrGkdDBTQY/VvQ2/ki6u'
where password_hash is null;

alter table user_account alter column password_hash set not null;
