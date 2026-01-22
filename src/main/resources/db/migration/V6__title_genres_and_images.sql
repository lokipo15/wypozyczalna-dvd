alter table title
    alter column genre type text,
    add column if not exists thumbnail_url text,
    add column if not exists image_url text;

