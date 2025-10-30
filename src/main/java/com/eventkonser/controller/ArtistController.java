package com.eventkonser.controller;

import com.eventkonser.model.Artist;
import com.eventkonser.service.ArtistService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArtistController {
    
    private final ArtistService artistService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Artist>>> getAllArtists() {
        return ResponseEntity.ok(ApiResponse.success("Success", artistService.getAllArtists()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Artist>> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", artistService.getArtistById(id)));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Artist>>> searchArtists(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success("Success", artistService.searchArtists(q)));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Artist>> createArtist(@RequestBody Artist artist) {
        return ResponseEntity.ok(ApiResponse.success("Artis berhasil dibuat", artistService.createArtist(artist)));
    }
}
