package com.eventkonser.service;

import com.eventkonser.model.Artist;
import com.eventkonser.repository.ArtistRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {
    
    private final ArtistRepository artistRepository;
    
    @Transactional(readOnly = true)
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Artist getArtistById(Long id) {
        return artistRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Artis tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Artist> searchArtists(String keyword) {
        return artistRepository.findByNamaArtisContainingIgnoreCase(keyword);
    }
    
    @Transactional(readOnly = true)
    public List<Artist> getArtistsByGenre(String genre) {
        return artistRepository.findByGenre(genre);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllGenres() {
        return artistRepository.findAllGenres();
    }
    
    @Transactional
    public Artist createArtist(Artist artist) {
        return artistRepository.save(artist);
    }
    
    @Transactional
    public Artist updateArtist(Long id, Artist artistDetails) {
        Artist artist = getArtistById(id);
        artist.setNamaArtis(artistDetails.getNamaArtis());
        artist.setGenre(artistDetails.getGenre());
        artist.setNegaraAsal(artistDetails.getNegaraAsal());
        artist.setKontak(artistDetails.getKontak());
        artist.setBio(artistDetails.getBio());
        artist.setFotoUrl(artistDetails.getFotoUrl());
        return artistRepository.save(artist);
    }
    
    @Transactional
    public void deleteArtist(Long id) {
        Artist artist = getArtistById(id);
        artistRepository.delete(artist);
    }
}
