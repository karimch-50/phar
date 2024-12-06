package com.pharma.fs.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.pharma.fs.data.entity.OrganisationType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganisationTypeService {

    public String getInformation();  
    public Optional<OrganisationType> get(UUID id);
    public  Page<OrganisationType> list(Pageable pageable);
    public Page<OrganisationType> list(Pageable pageable, Specification<OrganisationType> filter);
    public long count();
    // public Optional<Organisation> get(String cin);
    public OrganisationType create(OrganisationType entity);
    public OrganisationType update(OrganisationType entity);

    public void delete(UUID id);
    
    public OrganisationType createOrganisationType(OrganisationType orgtype);

    public List<OrganisationType> getOrganisationTypes();
}
