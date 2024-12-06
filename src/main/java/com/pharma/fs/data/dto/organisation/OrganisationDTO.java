package com.pharma.fs.data.dto.organisation;

import com.pharma.fs.data.entity.Organisation;

import java.util.UUID;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class OrganisationDTO {
    UUID id;
    String name;
    String timing;
    String ville;
    String address;
    String description;
    String code;

    public OrganisationDTO(Organisation organisation) {
        if (organisation == null) {
            return;
        }
        this.id = organisation.getId();
        this.name = organisation.getName();
        this.timing = organisation.getTiming();
        this.ville = organisation.getVille();
        this.address = organisation.getAddress();
        this.description = organisation.getDescription();
        this.code = organisation.getCode();
    }

    public OrganisationDTO toOrganisationDTO(Organisation organisation) {
        if (organisation == null) {
            return null; // or throw an exception depending on your use case
        }

        OrganisationDTO organisationDTO = new OrganisationDTO();

        // Mapping fields from Organisation to OrganisationDTO
        organisationDTO.setId(organisation.getId());
        organisationDTO.setName(organisation.getName());
        organisationDTO.setTiming(organisation.getTiming());
        organisationDTO.setVille(organisation.getVille());
        organisationDTO.setAddress(organisation.getAddress());
        organisationDTO.setDescription(organisation.getDescription());
        organisationDTO.setCode(organisation.getCode());
        return organisationDTO;
    }
}
