package com.pharma.fs.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pharma.fs.data.entity.OrganisationType;
import com.pharma.fs.data.repo.OrganisationTypeRepository;
import com.pharma.fs.data.service.OrganisationTypeService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganisationTypeServiceImpl implements OrganisationTypeService {

    @Autowired
    private OrganisationTypeRepository repository;

    @Override
    public String getInformation(){
        return "this has been called";
    }

    @Override
    public Page<OrganisationType> list(Pageable pageable) {
        return repository.findAll(pageable);
    }


    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public OrganisationType create(OrganisationType entity) {
        return repository.save(entity);
    }

    @Override
    public OrganisationType update(OrganisationType entity) {
        return repository.save(entity);
    }


    @Override
    public Page<OrganisationType> list(Pageable pageable, Specification<OrganisationType> filter) {
        return repository.findAll(filter, pageable);
    }

    @Override
    public OrganisationType createOrganisationType(OrganisationType patient) {
        return repository.save(patient);
    }


    @Override
    public Optional<OrganisationType> get(UUID id) {
        return repository.findById(id
        );
    }

    @Override
    public List<OrganisationType> getOrganisationTypes(){
        return repository.findAll();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
