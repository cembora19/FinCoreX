INSERT INTO assets (id, symbol, name, price)
VALUES
    (gen_random_uuid(), 'AAPL', 'Apple', 180.00),
    (gen_random_uuid(), 'BTC', 'Bitcoin', 60000.00),
    (gen_random_uuid(), 'GOLD', 'Gold', 2300.00)
ON CONFLICT (symbol) DO NOTHING;
