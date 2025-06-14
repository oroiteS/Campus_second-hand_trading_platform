use campus;
INSERT INTO `commodities` (
  `commodity_id`, 
  `commodity_name`, 
  `commodity_description`, 
  `category_id`, 
  `tags`, 
  `current_price`, 
  `commodity_status`, 
  `seller_id`, 
  `main_image_url`, 
  `image_list`, 
  `created_at`, 
  `updated_at`
) VALUES 
(
  'a1b2c3d4-e5f6-7a8b-9cde-f01a2b3c4d56', 
  'Apple iPhone 13 Pro', 
  '95新，无瑕疵，国行版本，充电次数少于100次', 
  1, 
  '["95 new", "China mainland version", "128GB","手机"]', 
  7999.00, 
  'on_sale', 
  's12345678', 
  'a1b2c3d4-e5f6-7a8b-9cde-f01a2b3c4d56_main.jpg', 
  '["a1b2c3d4-e5f6-7a8b-9cde-f01a2b3c4d56_1.png", "a1b2c3d4-e5f6-7a8b-9cde-f01a2b3c4d56_2.jpg"]', 
  NOW(), 
  NOW()
),
(
  'f9e8d7c6-b5a4-3210-4567-89ab0cde1f2g', 
  'Samsung Galaxy S22 Ultra', 
  '99新，仅拆封未使用，带全套配件', 
  1, 
  '["99 new", "International version", "256GB","手机"]', 
  8499.00, 
  'on_sale', 
  's87654321', 
  'f9e8d7c6-b5a4-3210-4567-89ab0cde1f2g_main.jpg', 
  '["f9e8d7c6-b5a4-3210-4567-89ab0cde1f2g_1.jpg"]', 
  NOW() - INTERVAL 1 DAY, 
  NOW()
);