CREATE TABLE assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    symbol VARCHAR(50) NOT NULL UNIQUE,

    name VARCHAR(255) NOT NULL
);