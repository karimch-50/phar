package com.pharma.fs.data.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pharma.fs.data.dto.organisation.OrganisationInfoDTO;
import com.pharma.fs.data.dto.organisation.OrganisationInfoMapper;
import com.pharma.fs.data.entity.Organisation;
import com.pharma.fs.data.enums.OrganisationTypeEnum;
import com.pharma.fs.data.repo.OrganisationRepository;
import com.pharma.fs.data.service.OrganisationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganisationServiceImpl implements OrganisationService {

    @Autowired
    private OrganisationRepository repository;

    @Override
    public String getInformation() {
        return "this has been called";
    }

    @Override
    public Page<Organisation> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Organisation create(Organisation entity) {
        return repository.save(entity);
    }

    @Override
    public Organisation update(Organisation entity) {
        return repository.save(entity);
    }

    @Override
    public Page<Organisation> list(Pageable pageable, Specification<Organisation> filter) {
        return repository.findAll(filter, pageable);
    }

    @Override
    public Optional<Organisation> get(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Organisation> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public Page<OrganisationInfoDTO> getOrganisationInfos(Pageable pageable)
    {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Organisation> root = cq.from(Organisation.class);

        cq.multiselect(
                root.get("id"),
                root.get("name"),
                root.get("ville"),
                root.get("address"),
                root.get("longitude"),
                root.get("latitude"),
                root.get("active"),
                root.get("created"));

        Predicate predicate = cb.conjunction();

        cq.where(predicate);

        List<Object[]> resultList = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        List<OrganisationInfoDTO> dtoList = new ArrayList<>();
        OrganisationInfoMapper mapper = new OrganisationInfoMapper();

        for (Object[] array : resultList) {
            dtoList = mapper.toOrganisationInfoDTO(array, dtoList);
        }

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Organisation.class)));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
