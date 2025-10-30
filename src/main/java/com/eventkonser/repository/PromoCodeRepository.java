package com.eventkonser.repository;

import com.eventkonser.model.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    
    // Find by code
    Optional<PromoCode> findByKode(String kode);
    
    // Find active promo codes
    @Query("SELECT p FROM PromoCode p WHERE p.active = true " +
           "AND :currentDate BETWEEN p.tanggalMulai AND p.tanggalSelesai " +
           "AND (p.maxPenggunaan IS NULL OR p.totalDigunakan < p.maxPenggunaan)")
    List<PromoCode> findActivePromoCodes(@Param("currentDate") LocalDate currentDate);
    
    // Validate promo code
    @Query("SELECT p FROM PromoCode p WHERE p.kode = :kode " +
           "AND p.active = true " +
           "AND :currentDate BETWEEN p.tanggalMulai AND p.tanggalSelesai " +
           "AND (p.maxPenggunaan IS NULL OR p.totalDigunakan < p.maxPenggunaan)")
    Optional<PromoCode> findValidPromoCode(@Param("kode") String kode, 
                                            @Param("currentDate") LocalDate currentDate);
    
    // Find expired promo codes
    @Query("SELECT p FROM PromoCode p WHERE p.tanggalSelesai < :currentDate")
    List<PromoCode> findExpiredPromoCodes(@Param("currentDate") LocalDate currentDate);
}