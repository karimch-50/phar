package com.pharma.fs.data.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.pharma.fs.data.dto.location.LocationDTO;
import com.pharma.fs.data.entity.Location;
import com.pharma.fs.data.enums.LocationType;

public interface LocationService {

    public Optional<Location> get(UUID id);

    public Set<Location> findAll();

    public Location update(Location entity);

    public void delete(UUID id);

    public Page<Location> list(Pageable pageable);

    public Page<Location> list(Pageable pageable, Specification<Location> filter);

    public int count();

    public Page<Location> getList(Pageable pageable, List<UUID> ids);
    public List<Location> findChildren(Location parent, List<LocationType> types);
    public List<LocationDTO> findLocationDTO();
}
