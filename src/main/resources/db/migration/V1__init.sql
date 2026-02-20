-- V1__init.sql

CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS read_books (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    google_volume_id  VARCHAR(64) NOT NULL,

    title             VARCHAR(512) NOT NULL,
    authors           TEXT,
    thumbnail_url     TEXT,
    published_date    VARCHAR(32),
    added_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_read_books_user_volume UNIQUE (user_id, google_volume_id)
);

CREATE INDEX IF NOT EXISTS idx_read_books_user_id ON read_books(user_id);
CREATE INDEX IF NOT EXISTS idx_read_books_user_added_at ON read_books(user_id, added_at DESC);
CREATE INDEX IF NOT EXISTS idx_read_books_user_volume_id ON read_books(user_id, google_volume_id);