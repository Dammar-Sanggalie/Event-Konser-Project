package com.eventkonser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SponsorDTO {
    private Long idSponsor;
    private Long idEvent;
    private String namaSponsor;
    private String kontak;
    private String jenisSponsor;
    private String logoUrl;
}
