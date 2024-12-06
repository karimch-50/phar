package com.labo.fs.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.labo.fs.data.dto.organisation.OrganisationInfoDTO;

import com.labo.fs.data.entity.Organisation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganisationService {
  public String getInformation();

  public Optional<Organisation> get(UUID id);

  public Page<Organisation> list(Pageable pageable);

  public Page<Organisation> list(Pageable pageable, Specification<Organisation> filter);

  public long count();

  public Organisation create(Organisation entity);

  public Organisation update(Organisation entity);

  public List<Organisation> findAll();

  public void delete(UUID id);

  public Page<OrganisationInfoDTO> getOrganisationInfos(Pageable pageable);

}
