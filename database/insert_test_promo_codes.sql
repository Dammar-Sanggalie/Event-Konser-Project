-- Insert test promo codes
INSERT INTO `promo_code` (`active`, `deskripsi`, `jenis_diskon`, `kode`, `max_penggunaan`, `min_pembelian`, `nilai_diskon`, `tanggal_mulai`, `tanggal_selesai`, `total_digunakan`) VALUES
(1, 'potongan 20%', 'PERCENTAGE', 'ADMIN', NULL, 0.00, 20.00, '2025-01-01', '2025-12-31', 0),
(1, 'Diskon besar besaran', 'PERCENTAGE', 'KELOMPOK 4', 100, 0.00, 15.00, '2025-01-01', '2025-12-20', 0);

-- Verify the inserted data
SELECT * FROM `promo_code`;
