ALTER TABLE wallets
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0,
    ADD CONSTRAINT ck_wallet_balance_non_negative CHECK (balance >= 0) NOT VALID,
    ADD CONSTRAINT ck_wallet_realized_profit_loss_valid CHECK (realized_profit_loss IS NOT NULL) NOT VALID;

ALTER TABLE wallet_assets
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0,
    ADD CONSTRAINT ck_wallet_asset_quantity_non_negative CHECK (quantity >= 0) NOT VALID,
    ADD CONSTRAINT ck_wallet_asset_average_price_non_negative CHECK (average_buy_price >= 0) NOT VALID;

ALTER TABLE assets
    ADD CONSTRAINT ck_asset_price_positive CHECK (price > 0) NOT VALID;

ALTER TABLE transactions
    ADD CONSTRAINT ck_transaction_amount_positive CHECK (amount > 0) NOT VALID;

CREATE INDEX IF NOT EXISTS idx_transactions_wallet_created_at
    ON transactions (wallet_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_audit_logs_wallet_created_at
    ON audit_logs (wallet_id, created_at DESC);
