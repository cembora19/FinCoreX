CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    details VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_audit_log_wallet
        FOREIGN KEY (wallet_id)
        REFERENCES wallets(id)
);
