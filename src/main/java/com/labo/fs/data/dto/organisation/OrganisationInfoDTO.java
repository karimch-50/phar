package com.labo.fs.data.dto.organisation;

import java.util.Date;
import java.util.UUID;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Setter
@lombok.Getter
public class OrganisationInfoDTO {
    UUID id;
    private String name;
    private String ville;
    private String address;
    private double longitude;
    private double latitude;
    private boolean active;
    private Date created;
}
