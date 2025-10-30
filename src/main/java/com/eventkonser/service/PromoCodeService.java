package com.eventkonser.service;

import com.eventkonser.model.PromoCode;
import com.eventkonser.repository.PromoCodeRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import com.eventkonser.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoCodeService {
    
    private final PromoCodeRepository promoCodeRepository;
    
    @Transactional(readOnly = true)
    public List<PromoCode> getActivePromoCodes(LocalDate currentDate) {
        return promoCodeRepository.findActivePromoCodes(currentDate);
    }
    
    @Transactional(readOnly = true)
    public PromoCode validatePromoCode(String kode, LocalDate currentDate) {
        return promoCodeRepository.findValidPromoCode(kode, currentDate)
            .orElseThrow(() -> new InvalidRequestException("Promo code tidak valid atau sudah kadaluarsa"));
    }
    
    @Transactional(readOnly = true)
    public PromoCode getPromoCodeById(Long id) {
        return promoCodeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promo code tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public PromoCode getPromoCodeByKode(String kode) {
        return promoCodeRepository.findByKode(kode)
            .orElseThrow(() -> new ResourceNotFoundException("Promo code tidak ditemukan: " + kode));
    }
    
    @Transactional
    public PromoCode createPromoCode(PromoCode promoCode) {
        if (promoCodeRepository.findByKode(promoCode.getKode()).isPresent()) {
            throw new InvalidRequestException("Kode promo sudah ada");
        }
        return promoCodeRepository.save(promoCode);
    }
    
    @Transactional
    public PromoCode updatePromoCode(Long id, PromoCode promoCodeDetails) {
        PromoCode promoCode = getPromoCodeById(id);
        promoCode.setKode(promoCodeDetails.getKode());
        promoCode.setDeskripsi(promoCodeDetails.getDeskripsi());
        promoCode.setJenisDiskon(promoCodeDetails.getJenisDiskon());
        promoCode.setNilaiDiskon(promoCodeDetails.getNilaiDiskon());
        promoCode.setMinPembelian(promoCodeDetails.getMinPembelian());
        promoCode.setMaxPenggunaan(promoCodeDetails.getMaxPenggunaan());
        promoCode.setTanggalMulai(promoCodeDetails.getTanggalMulai());
        promoCode.setTanggalSelesai(promoCodeDetails.getTanggalSelesai());
        promoCode.setActive(promoCodeDetails.getActive());
        return promoCodeRepository.save(promoCode);
    }
    
    @Transactional
    public void deletePromoCode(Long id) {
        PromoCode promoCode = getPromoCodeById(id);
        promoCodeRepository.delete(promoCode);
    }
    
    @Transactional
    public void usePromoCode(String kode) {
        PromoCode promoCode = promoCodeRepository.findByKode(kode)
            .orElseThrow(() -> new ResourceNotFoundException("Promo code tidak ditemukan"));
        
        // Increment usage
        promoCode.setTotalDigunakan((promoCode.getTotalDigunakan() != null ? promoCode.getTotalDigunakan() : 0) + 1);
        
        // Check if max usage reached
        if (promoCode.getMaxPenggunaan() != null && promoCode.getTotalDigunakan() >= promoCode.getMaxPenggunaan()) {
            promoCode.setActive(false);
        }
        
        promoCodeRepository.save(promoCode);
    }
    
    @Transactional(readOnly = true)
    public List<PromoCode> getExpiredPromoCodes(LocalDate currentDate) {
        return promoCodeRepository.findExpiredPromoCodes(currentDate);
    }
    
    @Transactional
    public void deactivateExpiredPromoCodes(LocalDate currentDate) {
        List<PromoCode> expiredCodes = getExpiredPromoCodes(currentDate);
        for (PromoCode promo : expiredCodes) {
            promo.setActive(false);
            promoCodeRepository.save(promo);
        }
    }
}