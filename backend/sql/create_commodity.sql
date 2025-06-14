CREATE TABLE `commodities` (
  `commodity_id` CHAR(36) NOT NULL COMMENT 'Commodity unique identifier (UUID v7)',
  `commodity_name` VARCHAR(100) NOT NULL COMMENT 'Commodity title (e.g., "Apple iPhone 13")',
  `commodity_description` TEXT NOT NULL COMMENT 'Detailed description (condition, defects, etc.)',
  `category_id` INT UNSIGNED NOT NULL COMMENT 'Foreign key referencing category table',
  `tags` JSON DEFAULT NULL COMMENT 'Array of tags (e.g., ["95 new", "China mainland version"])',
  `current_price` DECIMAL(10, 2) NOT NULL COMMENT 'Selling price (e.g., 3500.00)',
  `commodity_status` ENUM('on_sale', 'sold', 'off_sale') NOT NULL COMMENT 'Commodity status: on sale/sold/off sale',
  `seller_id` CHAR(9) NOT NULL COMMENT 'Foreign key referencing user table',
  `main_image_url` VARCHAR(255) NOT NULL COMMENT 'Main commodity image URL',
  `image_list` JSON DEFAULT NULL COMMENT 'Array of additional image URLs (optional)',
  `created_at` DATETIME NOT NULL COMMENT 'Commodity creation time',
  `updated_at` DATETIME NOT NULL COMMENT 'Last update time',
  PRIMARY KEY (`commodity_id`),
  KEY `idx_commodity_name` (`commodity_name`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_current_price` (`current_price`),
  KEY `idx_commodity_status` (`commodity_status`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_updated_at` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Commodity information table';
    