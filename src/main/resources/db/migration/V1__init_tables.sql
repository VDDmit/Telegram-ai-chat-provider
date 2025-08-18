CREATE TABLE IF NOT EXISTS users
(
    id                 BIGINT PRIMARY KEY,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    username           VARCHAR(32) UNIQUE,
    first_name         VARCHAR(255) NOT NULL,
    last_name          VARCHAR(255),
    language_code      VARCHAR(10),

    private_ai_api_key VARCHAR(255),
    model              VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);



CREATE TABLE IF NOT EXISTS prompts
(
    id                 UUID PRIMARY KEY,
    name               VARCHAR(100) NOT NULL,
    system_prompt      TEXT         NOT NULL,
    is_public          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by_user_id BIGINT       REFERENCES users (id) ON DELETE SET NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS messages
(
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role       BOOLEAN     NOT NULL, -- true = AI, false = user
    content    TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_messages_user_created ON messages (user_id, created_at DESC);


CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();