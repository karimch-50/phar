package com.pharma.fs.data.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.pharma.fs.data.entity.Location;
import com.pharma.fs.data.enums.LocationType;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID>, JpaSpecificationExecutor<Location> {
    Page<Location> findByTypeIn(List<LocationType> types, Pageable pageable);
}
