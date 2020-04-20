package com.aurum.casesintegrator.service;

import java.util.List;

import javax.management.InstanceAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.strategy.Criteria;
import com.aurum.casesintegrator.service.strategy.factory.FilterCriteria;
import com.aurum.casesintegrator.service.strategy.factory.FilterCriteriaFactory;
import com.aurum.casesintegrator.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import reactor.core.publisher.Flux;

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

    public Flux<Case> findByCriteria(final CaseCriteria caseCriteria) {
        final FilterCriteriaFactory criteriaFactory = new FilterCriteriaFactory(this.caseRepository);
        final FilterCriteria criteria = criteriaFactory.getCriteria(caseCriteria);
        final Flux<Case> filteredCases = new Criteria(criteria).filter()
                .filter(c -> caseCriteria.getFolder() == null || c.getFolder().toLowerCase().contains(caseCriteria.getFolder().toLowerCase()))
                .filter(c -> caseCriteria.getTitle() == null || c.getTitle().toLowerCase().contains(caseCriteria.getTitle().toLowerCase()))
                .parallel().sequential();

        if (StringUtils.isEmpty(caseCriteria.getDescription())) {
            return filteredCases;
        }
        return filteredCases.filter(c -> c.getDescription().toLowerCase().contains(caseCriteria.getDescription().toLowerCase()));
    }

}
