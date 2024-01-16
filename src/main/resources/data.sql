DELETE FROM transaction;
INSERT INTO transaction (id, data, created_at) VALUES
  (-1, '{ "amount": 100, "reference": "BM_2023_101" }', NOW()),
  (-2, '{ "amount": 3333, "reference": "", "sender": "Bankmonitor" }', NOW()),
  (-3, '{ "amount": -100, "reference": "BM_2023_101_BACK", "reason": "duplicate" }', NOW()),
  (-4, '{ "amount": 12345, "reference": "BM_2023_105" }', NOW()),
  (-5, '{ "amount": 54321, "sender": "Bankmonitor", "recipient": "John Doe" }', NOW());