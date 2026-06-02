CREATE TABLE wallet_assets (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    wallet_id UUID NOT NULL,

    asset_id UUID NOT NULL,

    quantity NUMERIC(19,4) NOT NULL,

    CONSTRAINT fk_wallet_asset_wallet
        FOREIGN KEY (wallet_id)
        REFERENCES wallets(id),

    CONSTRAINT fk_wallet_asset_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id),

    CONSTRAINT uk_wallet_asset
        UNIQUE (wallet_id, asset_id)
);