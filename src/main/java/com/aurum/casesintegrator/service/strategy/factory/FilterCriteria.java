package com.aurum.casesintegrator.service.strategy.factory;

import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.strategy.CriteriaStrategy;

public abstract class FilterCriteria implements CriteriaStrategy {

    protected CaseCriteria criteria;
    protected CaseRepository repository;

}
