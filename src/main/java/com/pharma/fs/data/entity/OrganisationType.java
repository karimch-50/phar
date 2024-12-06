package com.pharma.fs.data.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrganisationType extends AbstractEntity {
    private String code;
    private String source;
    private String display;
    private String definition;
    private String comment;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Organisation> organisations = new HashSet<>();

    private boolean active = true;
    private Date created = new Date();
    private Date updated = new Date();
    private String createdBy;
    private String updatedBy;
}
