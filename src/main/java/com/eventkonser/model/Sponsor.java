package com.eventkonser.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sponsor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sponsor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sponsor")
    private Long idSponsor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_event", nullable = false)
    @JsonBackReference
    private Event event;
    
    @Transient
    @JsonProperty("idEvent")
    private Long idEvent;
    
    @Column(name = "nama_sponsor", nullable = false, length = 100)
    private String namaSponsor;
    
    @Column(length = 100)
    private String kontak;
    
    @Column(name = "jenis_sponsor", length = 50)
    private String jenisSponsor;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @PostLoad
    public void postLoad() {
        if (this.event != null) {
            this.idEvent = this.event.getIdEvent();
        }
    }
}
