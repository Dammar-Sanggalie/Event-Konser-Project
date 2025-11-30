-- Migration: Add discount tracking fields to pembelian_tiket table
-- Purpose: Track discount amounts and promo codes applied during checkout
-- Date: 2025-11-30

ALTER TABLE `pembelian_tiket` 
ADD COLUMN `subtotal` DECIMAL(10,2) DEFAULT 0 AFTER `total_harga`,
ADD COLUMN `discount_amount` DECIMAL(10,2) DEFAULT 0 AFTER `subtotal`,
ADD COLUMN `promo_code` VARCHAR(50) DEFAULT NULL AFTER `discount_amount`,
ADD COLUMN `event_image_url` VARCHAR(255) DEFAULT NULL AFTER `event_name`,
ADD COLUMN `id_event` BIGINT(20) DEFAULT NULL AFTER `venue_name`;

-- Verify the changes
SHOW COLUMNS FROM `pembelian_tiket`;
