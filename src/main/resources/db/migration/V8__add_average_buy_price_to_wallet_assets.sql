ALTER TABLE wallet_assets
ADD COLUMN average_buy_price NUMERIC(19,4) NOT NULL DEFAULT 0;

UPDATE wallet_assets wa
SET average_buy_price = a.price
FROM assets a
WHERE wa.asset_id = a.id;
