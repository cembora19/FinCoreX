CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    wallet_id UUID NOT NULL,

    type VARCHAR(20) NOT NULL,

    amount NUMERIC(19,2) NOT NULL,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_transaction_wallet
        FOREIGN KEY (wallet_id)
        REFERENCES wallets(id)
);