package com.aurum.casesintegrator.repository;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import com.aurum.casesintegrator.domain.Case;

public interface CaseRepository extends DatastoreRepository<Case, Long> {

}
