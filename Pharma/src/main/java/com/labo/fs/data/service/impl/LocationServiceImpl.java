package com.labo.fs.data.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.labo.fs.data.dto.location.LocationDTO;
import com.labo.fs.data.entity.Location;
import com.labo.fs.data.entity.Organisation;
import com.labo.fs.data.enums.LocationStatus;
import com.labo.fs.data.enums.LocationType;
import com.labo.fs.data.repo.LocationRepository;
import com.labo.fs.data.service.LocationService;

import java.util.*;

import jakarta.transaction.Transactional;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    private LocationRepository repository;

    @Override
    public Set<Location> findAll() {
        return new HashSet<Location>(repository.findAll());
    }

    @Override
    public Optional<Location> get(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Location update(Location entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<Location> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Location> list(Pageable pageable, Specification<Location> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Location> getList(Pageable pageable, List<UUID> organisationIds){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Location> root = cq.from(Location.class);

        Predicate predicate = cb.conjunction();

        cq.multiselect(
                root.get("id"),
                root.get("name"));
        // get location id, name by organisation id
        if (organisationIds != null && !organisationIds.isEmpty()) {
            Join<Location, Organisation> join = root.join("organisation", JoinType.INNER);
            predicate = cb.and(predicate, join.get("id").in(organisationIds));
        }

        cq.where(predicate);
        // Execute query and get the result page
        List<Object[]> resultList = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // map the list of objects to Location
        List<Location> locationList = new ArrayList<>();
        for (Object[] array : resultList) {
            Location location = new Location();
            location.setId((UUID) array[0]);
            location.setName((String) array[1]);
            locationList.add(location);
        }

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Location.class)));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(locationList, pageable, totalCount);
    }

    @Override
    @Transactional
    public List<Location> findChildren(Location parent, List<LocationType> types) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> cq = cb.createQuery(Location.class);
        Root<Location> root = cq.from(Location.class);
        
        if (types != null && !types.isEmpty()) {
            Predicate typePredicate = root.get("type").in(types);
            cq.where(typePredicate);
        }
        // Define the predicate to find children of the given parent
        Predicate parentPredicate = cb.equal(root.get("parent"), parent);
        cq.where(parentPredicate);
        
        return entityManager.createQuery(cq).getResultList();
    }

    // @Override
    // @Transactional
    // public List<LocationDTO> findLocationDTO(){
    //     CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    //     CriteriaQuery<LocationDTO> cq = cb.createQuery(LocationDTO.class);
    //     Root<Location> root = cq.from(Location.class);
    //     // public class LocationDTO {
    //     //     private UUID id;
    //     //     private LocationType type;
    //     //     private String name;
    //     //     private LocationStatus status;
    //     //     private List<LocationDTO> children = new ArrayList<>();
    //     // }
    //     cq.multiselect(
    //             root.get("id"),
    //             root.get("type"),
    //             root.get("name"),
    //             root.get("status"));
    //     // Define the predicate to find children of the given parent
    //     Predicate parentPredicate = cb.isNull(root.get("parent"));
    //     cq.where(parentPredicate);
    //     // Execute query and get the result page
    //     List<LocationDTO> resultList = entityManager.createQuery(cq).getResultList();

    //     // print the result
    //     for (LocationDTO locationDTO : resultList) {
    //         System.out.println("Parent: " + locationDTO.getName());
    //         for (LocationDTO child : locationDTO.getChildren()) {
    //             System.out.println("Child: " + child.getName());
    //         }
    //     }
    //     return resultList;
    // }

    public List<LocationDTO> findLocationDTO() {
        // Build the criteria query
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LocationDTO> cq = cb.createQuery(LocationDTO.class);
        Root<Location> root = cq.from(Location.class);

        // Define the selection for LocationDTO
        cq.select(cb.construct(
                LocationDTO.class,
                root.get("id"),
                root.get("type"),
                root.get("name"),
                root.get("status")
        ));

        // Filter for top-level locations (locations without parents)
        cq.where(cb.isNull(root.get("parent")));

        // Execute the query
        List<LocationDTO> rootLocations = entityManager.createQuery(cq).getResultList();

        // Fetch children and populate recursively
        for (LocationDTO locationDTO : rootLocations) {
            populateChildren(locationDTO);
        }

        for (LocationDTO locationDTO : rootLocations) {
            System.out.println("Parent: " + locationDTO.getName());
            for (LocationDTO child : locationDTO.getChildren()) {
                System.out.println("Child: " + child.getName());
            }
        }

        return rootLocations;
    }

    private void populateChildren(LocationDTO parentDTO) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LocationDTO> cq = cb.createQuery(LocationDTO.class);
        Root<Location> root = cq.from(Location.class);

        // Define the selection for child LocationDTO
        cq.select(cb.construct(
                LocationDTO.class,
                root.get("id"),
                root.get("type"),
                root.get("name"),
                root.get("status")
        ));

        // Filter for children of the current parent
        cq.where(cb.equal(root.get("parent").get("id"), parentDTO.getId()));

        // Execute the query to fetch children
        List<LocationDTO> children = entityManager.createQuery(cq).getResultList();

        // Recursively populate children
        for (LocationDTO childDTO : children) {
            populateChildren(childDTO);
        }

        // Set the children to the parent DTO
        parentDTO.setChildren(children);
    }


}
