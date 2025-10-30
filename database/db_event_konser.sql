-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 30 Okt 2025 pada 03.08
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_event_konser`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `admin`
--

CREATE TABLE `admin` (
  `id_admin` int(11) NOT NULL,
  `id_pengguna` bigint(20) NOT NULL,
  `jabatan` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `admin`
--

INSERT INTO `admin` (`id_admin`, `id_pengguna`, `jabatan`) VALUES
(1, 1, 'Super Admin'),
(2, 2, 'Event Coordinator');

-- --------------------------------------------------------

--
-- Struktur dari tabel `artis`
--

CREATE TABLE `artis` (
  `id_artis` bigint(20) NOT NULL,
  `nama_artis` varchar(100) NOT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `negara_asal` varchar(50) DEFAULT NULL,
  `kontak` varchar(100) DEFAULT NULL,
  `bio` text DEFAULT NULL,
  `foto_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `artis`
--

INSERT INTO `artis` (`id_artis`, `nama_artis`, `genre`, `negara_asal`, `kontak`, `bio`, `foto_url`) VALUES
(1, 'Raisa', 'Pop', 'Indonesia', 'management@raisa.id', NULL, NULL),
(2, 'Ed Sheeran', 'Pop/Acoustic', 'Inggris', 'edsheeran@booking.uk', NULL, NULL),
(3, 'Tulus', 'Pop/Jazz', 'Indonesia', 'booking@tulus.com', NULL, NULL),
(4, 'Taylor Swift', 'Pop', 'Amerika', 'taylorswift@booking.us', NULL, NULL),
(5, 'Reality Club', 'Indie Pop', 'Indonesia', 'realityclub@booking.com', NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `event`
--

CREATE TABLE `event` (
  `id_event` bigint(20) NOT NULL,
  `id_kategori` bigint(20) DEFAULT NULL,
  `nama_event` varchar(200) NOT NULL,
  `deskripsi` text DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `id_venue` bigint(20) DEFAULT NULL,
  `penyelenggara` varchar(100) DEFAULT NULL,
  `banner_url` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `poster_url` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','ONGOING','UPCOMING') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `event`
--

INSERT INTO `event` (`id_event`, `id_kategori`, `nama_event`, `deskripsi`, `tanggal_mulai`, `tanggal_selesai`, `id_venue`, `penyelenggara`, `banner_url`, `created_at`, `poster_url`, `status`, `updated_at`) VALUES
(1, 1, 'Raisa Live in Concert 2025', 'Konser tunggal Raisa dengan lagu-lagu terbaru dan hits terdahulu', '2025-11-15', '2025-11-15', 1, 'Musik Indonesia Production', NULL, NULL, NULL, NULL, NULL),
(2, 1, 'Ed Sheeran Mathematics Tour Jakarta', 'Ed Sheeran hadir dengan Mathematics Tour di Jakarta', '2025-12-10', '2025-12-10', 2, 'International Music', NULL, NULL, NULL, NULL, NULL),
(3, 2, 'Jakarta Music Festival 2025', 'Festival musik terbesar di Jakarta dengan berbagai artis', '2025-12-01', '2025-12-03', 2, 'Jakarta Event Organizer', NULL, NULL, NULL, NULL, NULL),
(4, 3, 'Tulus Acoustic Night', 'Pertunjukan akustik intimate Tulus', '2025-10-28', '2025-10-28', 5, 'Intimate Music', NULL, NULL, NULL, NULL, NULL),
(5, 1, 'Taylor Swift Eras Tour Jakarta', 'Taylor Swift Eras Tour di Indonesia', '2026-02-14', '2026-02-14', 2, 'Global Entertainment', NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `event_artis`
--

CREATE TABLE `event_artis` (
  `id_event_artis` int(11) NOT NULL,
  `id_event` bigint(20) NOT NULL,
  `id_artis` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `event_artis`
--

INSERT INTO `event_artis` (`id_event_artis`, `id_event`, `id_artis`) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 1),
(4, 3, 3),
(5, 4, 3);

-- --------------------------------------------------------

--
-- Struktur dari tabel `jadwal_event`
--

CREATE TABLE `jadwal_event` (
  `id_jadwal` bigint(20) NOT NULL,
  `id_event` bigint(20) NOT NULL,
  `tanggal` date DEFAULT NULL,
  `jam_mulai` time DEFAULT NULL,
  `jam_selesai` time DEFAULT NULL,
  `keterangan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `jadwal_event`
--

INSERT INTO `jadwal_event` (`id_jadwal`, `id_event`, `tanggal`, `jam_mulai`, `jam_selesai`, `keterangan`) VALUES
(1, 1, '2025-11-15', '19:00:00', '22:00:00', 'Konser utama Raisa'),
(2, 2, '2025-12-10', '19:30:00', '22:30:00', 'Ed Sheeran live performance'),
(3, 3, '2025-12-01', '16:00:00', '23:00:00', 'Hari pertama festival'),
(4, 3, '2025-12-02', '14:00:00', '23:00:00', 'Hari kedua festival'),
(5, 4, '2025-10-28', '20:00:00', '22:30:00', 'Tulus acoustic performance');

-- --------------------------------------------------------

--
-- Struktur dari tabel `kategori_event`
--

CREATE TABLE `kategori_event` (
  `id_kategori` bigint(20) NOT NULL,
  `nama_kategori` varchar(50) NOT NULL,
  `deskripsi` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `kategori_event`
--

INSERT INTO `kategori_event` (`id_kategori`, `nama_kategori`, `deskripsi`) VALUES
(1, 'Konser Musik', 'Event konser musik berbagai genre'),
(2, 'Festival', 'Festival musik dengan multiple artis'),
(3, 'Acoustic Session', 'Pertunjukan musik akustik intimate'),
(4, 'DJ Performance', 'Pertunjukan DJ dan musik elektronik'),
(5, 'Classical Concert', 'Konser musik klasik dan orkestra');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pembayaran`
--

CREATE TABLE `pembayaran` (
  `id_pembayaran` bigint(20) NOT NULL,
  `id_pembelian` bigint(20) NOT NULL,
  `metode_pembayaran` varchar(50) NOT NULL,
  `tanggal_bayar` datetime DEFAULT NULL,
  `jumlah_bayar` decimal(10,2) NOT NULL,
  `status_pembayaran` enum('EXPIRED','FAILED','PENDING','REFUNDED','SUCCESS') NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `expired_at` datetime(6) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `payment_gateway_id` varchar(255) DEFAULT NULL,
  `payment_url` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pembayaran`
--

INSERT INTO `pembayaran` (`id_pembayaran`, `id_pembelian`, `metode_pembayaran`, `tanggal_bayar`, `jumlah_bayar`, `status_pembayaran`, `created_at`, `expired_at`, `notes`, `payment_gateway_id`, `payment_url`, `updated_at`) VALUES
(1, 1, 'transfer', '2025-09-15 14:35:00', 500000.00, 'SUCCESS', NULL, NULL, NULL, NULL, NULL, NULL),
(2, 2, 'e-wallet', '2025-09-16 09:18:00', 1200000.00, 'SUCCESS', NULL, NULL, NULL, NULL, NULL, NULL),
(3, 3, 'kartu kredit', '2025-09-17 16:50:00', 500000.00, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL),
(4, 4, 'transfer', '2025-09-18 11:25:00', 5000000.00, 'SUCCESS', NULL, NULL, NULL, NULL, NULL, NULL),
(5, 5, 'e-wallet', '2025-09-19 13:12:00', 180000.00, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `pembelian_tiket`
--

CREATE TABLE `pembelian_tiket` (
  `id_pembelian` bigint(20) NOT NULL,
  `id_pengguna` bigint(20) DEFAULT NULL,
  `id_tiket` bigint(20) NOT NULL,
  `jumlah` int(11) DEFAULT NULL,
  `total_harga` decimal(10,2) NOT NULL,
  `tanggal_pembelian` datetime DEFAULT current_timestamp(),
  `status` enum('CANCELLED','EXPIRED','PAID','PENDING','REFUNDED','USED') NOT NULL,
  `event_date` varchar(255) DEFAULT NULL,
  `event_name` varchar(255) DEFAULT NULL,
  `expired_at` datetime(6) DEFAULT NULL,
  `qr_code` varchar(255) DEFAULT NULL,
  `ticket_type` varchar(255) DEFAULT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  `venue_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pembelian_tiket`
--

INSERT INTO `pembelian_tiket` (`id_pembelian`, `id_pengguna`, `id_tiket`, `jumlah`, `total_harga`, `tanggal_pembelian`, `status`, `event_date`, `event_name`, `expired_at`, `qr_code`, `ticket_type`, `used_at`, `venue_name`) VALUES
(1, 3, 1, 2, 500000.00, '2025-09-15 14:30:00', 'PAID', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 4, 3, 1, 1200000.00, '2025-09-16 09:15:00', 'PAID', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 5, 2, 1, 500000.00, '2025-09-17 16:45:00', 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(4, 3, 4, 2, 5000000.00, '2025-09-18 11:20:00', 'PAID', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(5, 4, 5, 1, 180000.00, '2025-09-19 13:10:00', 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `pengguna`
--

CREATE TABLE `pengguna` (
  `id_pengguna` bigint(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `no_hp` varchar(20) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `role` enum('ADMIN','ORGANIZER','USER') NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `jabatan` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pengguna`
--

INSERT INTO `pengguna` (`id_pengguna`, `nama`, `email`, `password`, `no_hp`, `alamat`, `role`, `created_at`, `jabatan`, `updated_at`) VALUES
(1, 'Admin Utama', 'admin@eventkonser.com', '0192023a7bbd73250516f069df18b500', '081234567890', 'Jakarta', 'ADMIN', NULL, NULL, NULL),
(2, 'Panitia Event', 'panitia@eventkonser.com', '913caf40bf0473d4f0f7a527cd44dea9', '081234567891', 'Jakarta', '', NULL, NULL, NULL),
(3, 'Budi Santoso', 'budi@email.com', '482c811da5d5b4bc6d497ffa98491e38', '081234567892', 'Jl. Sudirman No. 123, Jakarta', 'USER', NULL, NULL, NULL),
(4, 'Siti Nurhaliza', 'siti@email.com', '482c811da5d5b4bc6d497ffa98491e38', '081234567893', 'Jl. Thamrin No. 456, Jakarta', 'USER', NULL, NULL, NULL),
(5, 'Andi Wijaya', 'andi@email.com', '482c811da5d5b4bc6d497ffa98491e38', '081234567894', 'Jl. Gatot Subroto No. 789, Jakarta', 'USER', NULL, NULL, NULL),
(6, 'Test User', 'testuser@example.com', 'password123', '081234567890', NULL, 'USER', '2025-10-15 20:50:50.000000', NULL, '2025-10-15 20:50:50.000000'),
(7, 'DS', 'keren@gmail.com', '12345678', '0897762828', 'maospati gg alfamart', 'USER', '2025-10-29 22:42:39.000000', NULL, '2025-10-29 22:42:39.000000');

-- --------------------------------------------------------

--
-- Struktur dari tabel `promo_code`
--

CREATE TABLE `promo_code` (
  `id_promo` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `deskripsi` text DEFAULT NULL,
  `jenis_diskon` enum('FIXED','PERCENTAGE') NOT NULL,
  `kode` varchar(50) NOT NULL,
  `max_penggunaan` int(11) DEFAULT NULL,
  `min_pembelian` decimal(10,2) DEFAULT NULL,
  `nilai_diskon` decimal(10,2) NOT NULL,
  `tanggal_mulai` date NOT NULL,
  `tanggal_selesai` date NOT NULL,
  `total_digunakan` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `sponsor`
--

CREATE TABLE `sponsor` (
  `id_sponsor` bigint(20) NOT NULL,
  `nama_sponsor` varchar(100) NOT NULL,
  `kontak` varchar(100) DEFAULT NULL,
  `jenis_sponsor` varchar(50) DEFAULT NULL,
  `id_event` bigint(20) NOT NULL,
  `logo_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `sponsor`
--

INSERT INTO `sponsor` (`id_sponsor`, `nama_sponsor`, `kontak`, `jenis_sponsor`, `id_event`, `logo_url`) VALUES
(1, 'Bank Mandiri', 'sponsor@bankmandiri.co.id', 'Platinum Sponsor', 1, NULL),
(2, 'Telkomsel', 'partnership@telkomsel.com', 'Gold Sponsor', 1, NULL),
(3, 'Coca Cola', 'events@cocacola.co.id', 'Official Beverage Partner', 2, NULL),
(4, 'Samsung Indonesia', 'marketing@samsung.co.id', 'Technology Partner', 3, NULL),
(5, 'Grab Indonesia', 'events@grab.com', 'Official Transportation Partner', 4, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tiket`
--

CREATE TABLE `tiket` (
  `id_tiket` bigint(20) NOT NULL,
  `id_event` bigint(20) NOT NULL,
  `jenis_tiket` varchar(50) NOT NULL,
  `harga` decimal(10,2) NOT NULL,
  `stok` int(11) DEFAULT NULL,
  `deskripsi` text DEFAULT NULL,
  `max_pembelian` int(11) DEFAULT NULL,
  `status` enum('AVAILABLE','DISABLED','SOLD_OUT') DEFAULT NULL,
  `stok_awal` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tiket`
--

INSERT INTO `tiket` (`id_tiket`, `id_event`, `jenis_tiket`, `harga`, `stok`, `deskripsi`, `max_pembelian`, `status`, `stok_awal`) VALUES
(1, 1, 'Reguler', 250000.00, 8000, NULL, NULL, NULL, NULL),
(2, 1, 'VIP', 500000.00, 1500, NULL, NULL, NULL, NULL),
(3, 2, 'Reguler', 1200000.00, 40000, NULL, NULL, NULL, NULL),
(4, 2, 'VIP', 2500000.00, 8000, NULL, NULL, NULL, NULL),
(5, 4, 'Reguler', 180000.00, 2500, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `venue`
--

CREATE TABLE `venue` (
  `id_venue` bigint(20) NOT NULL,
  `nama_venue` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `kapasitas` int(11) DEFAULT NULL,
  `kota` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `venue`
--

INSERT INTO `venue` (`id_venue`, `nama_venue`, `alamat`, `kapasitas`, `kota`) VALUES
(1, 'Jakarta Convention Center', 'Jl. Gatot Subroto, Senayan, Jakarta', 15000, 'Jakarta'),
(2, 'Gelora Bung Karno', 'Jl. Pintu Satu Senayan, Jakarta', 80000, 'Jakarta'),
(3, 'ICE BSD', 'Jl. BSD Grand Boulevard, Tangerang', 12000, 'Tangerang'),
(4, 'Trans Studio Bandung', 'Jl. Gatot Subroto No.289, Bandung', 8000, 'Bandung'),
(5, 'Balai Sarbini', 'Jl. Sisingamangaraja, Jakarta', 3000, 'Jakarta');

-- --------------------------------------------------------

--
-- Struktur dari tabel `wishlist`
--

CREATE TABLE `wishlist` (
  `id_wishlist` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `reminder_sent` bit(1) DEFAULT NULL,
  `id_event` bigint(20) NOT NULL,
  `id_pengguna` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_admin`),
  ADD KEY `id_pengguna` (`id_pengguna`);

--
-- Indeks untuk tabel `artis`
--
ALTER TABLE `artis`
  ADD PRIMARY KEY (`id_artis`);

--
-- Indeks untuk tabel `event`
--
ALTER TABLE `event`
  ADD PRIMARY KEY (`id_event`),
  ADD KEY `id_kategori` (`id_kategori`),
  ADD KEY `id_venue` (`id_venue`);

--
-- Indeks untuk tabel `event_artis`
--
ALTER TABLE `event_artis`
  ADD PRIMARY KEY (`id_event_artis`),
  ADD KEY `id_event` (`id_event`),
  ADD KEY `id_artis` (`id_artis`);

--
-- Indeks untuk tabel `jadwal_event`
--
ALTER TABLE `jadwal_event`
  ADD PRIMARY KEY (`id_jadwal`),
  ADD KEY `id_event` (`id_event`);

--
-- Indeks untuk tabel `kategori_event`
--
ALTER TABLE `kategori_event`
  ADD PRIMARY KEY (`id_kategori`);

--
-- Indeks untuk tabel `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD PRIMARY KEY (`id_pembayaran`),
  ADD KEY `id_pembelian` (`id_pembelian`);

--
-- Indeks untuk tabel `pembelian_tiket`
--
ALTER TABLE `pembelian_tiket`
  ADD PRIMARY KEY (`id_pembelian`),
  ADD UNIQUE KEY `UK44748x9xfwugwhcsbatelkstt` (`qr_code`),
  ADD KEY `id_pengguna` (`id_pengguna`),
  ADD KEY `id_tiket` (`id_tiket`);

--
-- Indeks untuk tabel `pengguna`
--
ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id_pengguna`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indeks untuk tabel `promo_code`
--
ALTER TABLE `promo_code`
  ADD PRIMARY KEY (`id_promo`),
  ADD UNIQUE KEY `UKle6lgyx61xmjaryo2m8fv122x` (`kode`);

--
-- Indeks untuk tabel `sponsor`
--
ALTER TABLE `sponsor`
  ADD PRIMARY KEY (`id_sponsor`),
  ADD KEY `id_event` (`id_event`);

--
-- Indeks untuk tabel `tiket`
--
ALTER TABLE `tiket`
  ADD PRIMARY KEY (`id_tiket`),
  ADD KEY `id_event` (`id_event`);

--
-- Indeks untuk tabel `venue`
--
ALTER TABLE `venue`
  ADD PRIMARY KEY (`id_venue`);

--
-- Indeks untuk tabel `wishlist`
--
ALTER TABLE `wishlist`
  ADD PRIMARY KEY (`id_wishlist`),
  ADD UNIQUE KEY `UKj7rafte9r8fier5lwknb7cy77` (`id_pengguna`,`id_event`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `admin`
--
ALTER TABLE `admin`
  MODIFY `id_admin` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `artis`
--
ALTER TABLE `artis`
  MODIFY `id_artis` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `event`
--
ALTER TABLE `event`
  MODIFY `id_event` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `event_artis`
--
ALTER TABLE `event_artis`
  MODIFY `id_event_artis` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `jadwal_event`
--
ALTER TABLE `jadwal_event`
  MODIFY `id_jadwal` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `kategori_event`
--
ALTER TABLE `kategori_event`
  MODIFY `id_kategori` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `pembayaran`
--
ALTER TABLE `pembayaran`
  MODIFY `id_pembayaran` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `pembelian_tiket`
--
ALTER TABLE `pembelian_tiket`
  MODIFY `id_pembelian` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `pengguna`
--
ALTER TABLE `pengguna`
  MODIFY `id_pengguna` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT untuk tabel `promo_code`
--
ALTER TABLE `promo_code`
  MODIFY `id_promo` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `sponsor`
--
ALTER TABLE `sponsor`
  MODIFY `id_sponsor` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `tiket`
--
ALTER TABLE `tiket`
  MODIFY `id_tiket` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `venue`
--
ALTER TABLE `venue`
  MODIFY `id_venue` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `wishlist`
--
ALTER TABLE `wishlist`
  MODIFY `id_wishlist` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`);

--
-- Ketidakleluasaan untuk tabel `event`
--
ALTER TABLE `event`
  ADD CONSTRAINT `event_ibfk_1` FOREIGN KEY (`id_kategori`) REFERENCES `kategori_event` (`id_kategori`),
  ADD CONSTRAINT `event_ibfk_2` FOREIGN KEY (`id_venue`) REFERENCES `venue` (`id_venue`);

--
-- Ketidakleluasaan untuk tabel `event_artis`
--
ALTER TABLE `event_artis`
  ADD CONSTRAINT `event_artis_ibfk_1` FOREIGN KEY (`id_event`) REFERENCES `event` (`id_event`),
  ADD CONSTRAINT `event_artis_ibfk_2` FOREIGN KEY (`id_artis`) REFERENCES `artis` (`id_artis`);

--
-- Ketidakleluasaan untuk tabel `jadwal_event`
--
ALTER TABLE `jadwal_event`
  ADD CONSTRAINT `jadwal_event_ibfk_1` FOREIGN KEY (`id_event`) REFERENCES `event` (`id_event`);

--
-- Ketidakleluasaan untuk tabel `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD CONSTRAINT `pembayaran_ibfk_1` FOREIGN KEY (`id_pembelian`) REFERENCES `pembelian_tiket` (`id_pembelian`);

--
-- Ketidakleluasaan untuk tabel `pembelian_tiket`
--
ALTER TABLE `pembelian_tiket`
  ADD CONSTRAINT `pembelian_tiket_ibfk_1` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`),
  ADD CONSTRAINT `pembelian_tiket_ibfk_2` FOREIGN KEY (`id_tiket`) REFERENCES `tiket` (`id_tiket`);

--
-- Ketidakleluasaan untuk tabel `sponsor`
--
ALTER TABLE `sponsor`
  ADD CONSTRAINT `sponsor_ibfk_1` FOREIGN KEY (`id_event`) REFERENCES `event` (`id_event`);

--
-- Ketidakleluasaan untuk tabel `tiket`
--
ALTER TABLE `tiket`
  ADD CONSTRAINT `tiket_ibfk_1` FOREIGN KEY (`id_event`) REFERENCES `event` (`id_event`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
