CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

drop table profile_full;

CREATE TABLE IF NOT EXISTS profile_full (
    embedding_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    text text,
    metadata json,
    embedding vector(768)
    );

CREATE INDEX ON profile_full USING HNSW (embedding vector_cosine_ops);
alter table profile_full
    owner to sbbella;