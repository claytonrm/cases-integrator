package com.aurum.casesintegrator.service.strategy;

import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.data.domain.PageRequest;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.strategy.factory.FilterCriteria;

import reactor.core.publisher.Flux;

public class CustomerAndLabelsCriteriaStrategy extends FilterCriteria implements CriteriaStrategy {

    public CustomerAndLabelsCriteriaStrategy(final CaseCriteria criteria, final CaseRepository repository) {
        super.criteria = criteria;
        super.repository = repository;
    }

    @Override
    public Flux<Case> filter() {
        return super.filterByFullTextSearching(
                super.repository.findByCustomerAndLabelsContainingAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                        super.criteria.getCustomer(),
                        super.criteria.getLabels(),
                        super.criteria.getFrom().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        super.criteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        PageRequest.of(super.criteria.getPage(), super.criteria.getLimit())
                )
        );
    }
}
