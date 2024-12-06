package com.pharma.fs.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.pharma.fs.data.entity.Organisation;

import java.util.Optional;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, UUID>, JpaSpecificationExecutor<Organisation> {
    Optional<Organisation> findByName(String name);
}
