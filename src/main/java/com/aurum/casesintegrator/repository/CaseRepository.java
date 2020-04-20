package com.aurum.casesintegrator.repository;

import java.util.List;

import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;
import org.springframework.data.domain.Pageable;

import com.aurum.casesintegrator.domain.Case;

import reactor.core.publisher.Flux;

public interface CaseRepository extends FirestoreReactiveRepository<Case> {

    Flux<Case> findByCustomerAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(final String customer, final Long startInstant, final Long endInstant, final Pageable pageable);

    Flux<Case> findByLabelsContainingAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(final List<String> labels, final Long startInstant, final Long endInstant, final Pageable pageable);

    Flux<Case> findByAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(final String accessType, final Long startInstant, final Long endInstant, final Pageable pageable);

    Flux<Case> findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(final Long startInstant, final Long endInstant, final Pageable pageable);

    Flux<Case> findByCustomerAndLabelsContainingAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(final String customer, final List<String> labels, final String accessType,
                                                                                                                                 final Long startInstant, final Long endInstant, final Pageable pageable);

    Flux<Case> findByCustomerAndLabelsContainingAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(final String customer, final List<String> labels, final Long startInstant,
                                                                                                                    final Long endInstant, final Pageable pageable);

    Flux<Case> findByCustomerAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(final String customer, final String accessType, final Long startInstant,
                                                                                                              final Long endInstant, final Pageable pageable);

    Flux<Case> findByLabelsContainingAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(final List<String> labels, final String accessType, final Long startInstant,
                                                                                                                      final Long endInstant, final Pageable pageable);
}
