DROP TABLE IF EXISTS knowledge;
DROP TABLE IF EXISTS chat_cache;


CREATE TABLE IF NOT EXISTS knowledge
(
    id        SERIAL PRIMARY KEY,
    source    VARCHAR(255),
    content   TEXT,
    metadata  TEXT,
    embedding FLOAT8[]
);

CREATE TABLE IF NOT EXISTS chat_cache
(
    id                  SERIAL PRIMARY KEY,
    normalized_question TEXT NOT NULL,
    original_question   TEXT NOT NULL,
    game_name           VARCHAR(255),
    team_number         INTEGER,
    answer              TEXT NOT NULL,
    sources             JSONB,
    embedding           FLOAT8[]
);

CREATE UNIQUE INDEX IF NOT EXISTS uniq_chat_cache_entry
    ON chat_cache (normalized_question, game_name, team_number);