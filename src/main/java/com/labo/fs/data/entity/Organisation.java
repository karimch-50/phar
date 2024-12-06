package com.labo.fs.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Organisation extends AbstractEntity {

    private String name;
    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;
    private String ville;
    private String address;
    private String timing;
    private String code;
    @Email(message = "Email invalide")
    private String email;
    private String phone;

    private double longitude;
    private double latitude;

    @Lob
    private byte[] photo;

    @ManyToOne
    private OrganisationType organisationType;
    
    @OneToMany(fetch = FetchType.LAZY)
    private Set<Location> locations = new HashSet<>();

    private boolean active = true;
    private Date created = new Date();
    private Date updated = new Date();
    private String createdBy;
    private String updatedBy;
}
