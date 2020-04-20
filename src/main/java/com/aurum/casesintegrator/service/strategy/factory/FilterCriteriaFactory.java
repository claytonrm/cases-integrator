package com.aurum.casesintegrator.service.strategy.factory;

import org.springframework.util.CollectionUtils;

import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.strategy.AccessTypeCriteriaStrategy;
import com.aurum.casesintegrator.service.strategy.CreationDateCriteriaStrategy;
import com.aurum.casesintegrator.service.strategy.CustomerAndAccessTypeCriteriaStrategy;
import com.aurum.casesintegrator.service.strategy.CustomerAndLabelsCriteriaStrategy;
import com.aurum.casesintegrator.service.strategy.CustomerCriteriaStrategy;
import com.aurum.casesintegrator.service.strategy.FullIndexCriteriaStrategy;
import com.aurum.casesintegrator.service.strategy.LabelsAndAccessTypeCriteriaStrategy;
import com.aurum.casesintegrator.service.strategy.LabelsCriteriaStrategy;

public class FilterCriteriaFactory {

    private CaseRepository repository;

    public FilterCriteriaFactory(final CaseRepository repository) {
        this.repository = repository;
    }

    public FilterCriteria getCriteria(final CaseCriteria caseCriteria) {
        if (caseCriteria.getCustomer() != null) {
            return createCriteriaBasedOnCompositeCustomerIndex(caseCriteria);
        }

        if (!CollectionUtils.isEmpty(caseCriteria.getLabels())) {
            return createCriteriaBasedOnLabelsCompositeIndex(caseCriteria);
        }

        if (caseCriteria.getAccessType() != null) {
            return new AccessTypeCriteriaStrategy(caseCriteria, repository);
        }

        return new CreationDateCriteriaStrategy(caseCriteria, repository);
    }

    private FilterCriteria createCriteriaBasedOnLabelsCompositeIndex(CaseCriteria caseCriteria) {
        if (caseCriteria.getAccessType() != null) {
            return new LabelsAndAccessTypeCriteriaStrategy(caseCriteria, repository);
        }
        return new LabelsCriteriaStrategy(caseCriteria, repository);
    }

    private FilterCriteria createCriteriaBasedOnCompositeCustomerIndex(CaseCriteria caseCriteria) {
        if (!CollectionUtils.isEmpty(caseCriteria.getLabels()) && caseCriteria.getAccessType() != null) {
            return new FullIndexCriteriaStrategy(caseCriteria, repository);
        }

        if (!CollectionUtils.isEmpty(caseCriteria.getLabels())) {
            return new CustomerAndLabelsCriteriaStrategy(caseCriteria, repository);
        }

        if (caseCriteria.getAccessType() != null) {
            return new CustomerAndAccessTypeCriteriaStrategy(caseCriteria, repository);
        }
        return new CustomerCriteriaStrategy(caseCriteria, repository);
    }

}
