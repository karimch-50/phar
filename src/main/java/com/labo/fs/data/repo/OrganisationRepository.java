package com.labo.fs.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.labo.fs.data.entity.Organisation;

import java.util.Optional;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, UUID>, JpaSpecificationExecutor<Organisation> {
    Optional<Organisation> findByName(String name);
}
