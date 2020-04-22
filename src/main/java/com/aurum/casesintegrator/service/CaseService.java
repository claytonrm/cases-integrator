package com.aurum.casesintegrator.service;

import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aurum.casesintegrator.domain.AccessType;
import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.strategy.Criteria;
import com.aurum.casesintegrator.service.strategy.factory.FilterCriteria;
import com.aurum.casesintegrator.service.strategy.factory.FilterCriteriaFactory;
import com.aurum.casesintegrator.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final BatchCaseService batchCaseService;

    @Autowired
    public CaseService(final CaseRepository caseRepository, final BatchCaseService batchCaseService) {
        this.caseRepository = caseRepository;
        this.batchCaseService = batchCaseService;
    }

    public Flux<Case> create(final List<Case> cases) throws InstanceAlreadyExistsException {
        return this.batchCaseService.create(cases);
    }

    public List<Case> getExtractedCasesFrom(final String singleOrMultiple) {
        final List<Case> multipleCases = JsonUtil.fromString(singleOrMultiple, new TypeReference<>() {});
        if (multipleCases != null) {
            return multipleCases;
        }
        final Case singleCase = JsonUtil.fromString(singleOrMultiple, new TypeReference<>() {});
        return singleCase != null ? List.of(singleCase) : List.of();
    }

    public void updateAllFields(final Case newCaseData) {
        if (newCaseData.getId() == null) {
            throw new IllegalArgumentException("Field id must be filled.");
        }

        if (this.caseRepository.findById(newCaseData.getId()).blockOptional().isEmpty()) {
            throw new IllegalArgumentException("Case not found on database.");
        }

        this.caseRepository.save(newCaseData);
    }

    public Mono<Case> findById(final String id) throws InstanceNotFoundException {
        final Mono<Case> legalCase = this.caseRepository.findById(id);
        if (!legalCase.blockOptional().isPresent()) {
            throw new InstanceNotFoundException(String.format("Resource %s does not exist.", id));
        }
        return legalCase;
    }

    public Flux<Case> findByCriteria(final CaseCriteria caseCriteria) {
        validateParams(caseCriteria);

        final FilterCriteriaFactory criteriaFactory = new FilterCriteriaFactory(this.caseRepository);
        final FilterCriteria criteria = criteriaFactory.getCriteria(caseCriteria);

        return new Criteria(criteria).filter();
    }

    private void validateParams(CaseCriteria caseCriteria) {
        try {
            if (caseCriteria.getAccessType() != null) {
                AccessType.valueOf(caseCriteria.getAccessType());
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(String.format("Invalid Access Type param. Available options are [%s, %s]",
                    AccessType.PUBLIC.name(), AccessType.PRIVATE.name()
            ));
        }
    }
}
