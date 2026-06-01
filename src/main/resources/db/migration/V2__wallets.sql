CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    balance NUMERIC(19,2) DEFAULT 0,

    CONSTRAINT fk_wallet_user
    FOREIGN KEY (user_id) REFERENCES users(id)
);