package com.labo.fs.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.labo.fs.data.entity.OrganisationType;

import java.util.Optional;
import java.util.UUID;

public interface OrganisationTypeRepository extends JpaRepository<OrganisationType, UUID> , JpaSpecificationExecutor<OrganisationType> {
    Optional<OrganisationType> findByCode(String code);
}