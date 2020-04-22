package com.aurum.casesintegrator.service.strategy.factory;

import org.springframework.util.StringUtils;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.strategy.CriteriaStrategy;

import reactor.core.publisher.Flux;

public abstract class FilterCriteria implements CriteriaStrategy {

    protected CaseCriteria criteria;
    protected CaseRepository repository;

    private Flux<Case> fullTextFilterByDescription(final Flux<Case> cases) {
        if (this.criteria.getDescription() == null) {
            return cases;
        }
        return cases.filter(c -> c.getDescription() != null && c.getDescription().toLowerCase().contains(this.criteria.getDescription().toLowerCase()));
    }

    protected Flux<Case> filterByFullTextSearching(final Flux<Case> cases) {
        if (cases == null) {
            return Flux.empty();
        }
        return fullTextFilterByDescription(cases)
                .filter(c -> this.criteria.getTitle() == null || c.getTitle() != null
                        && c.getTitle().toLowerCase().contains(this.criteria.getTitle().toLowerCase())
                )
                .filter(c -> this.criteria.getFolder() == null || c.getFolder() != null
                        && c.getFolder().toLowerCase().contains(this.criteria.getFolder().toLowerCase()
                ));
    }

    protected boolean isFullTextSearch() {
        return !StringUtils.isEmpty(this.criteria.getFolder())
                || !StringUtils.isEmpty(this.criteria.getTitle())
                || !StringUtils.isEmpty(this.criteria.getDescription());
    }

}
