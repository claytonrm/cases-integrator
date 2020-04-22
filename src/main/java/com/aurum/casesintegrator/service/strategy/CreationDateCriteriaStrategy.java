package com.aurum.casesintegrator.service.strategy;

import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.strategy.factory.FilterCriteria;

import reactor.core.publisher.Flux;

public class CreationDateCriteriaStrategy extends FilterCriteria implements CriteriaStrategy {

    public CreationDateCriteriaStrategy(final CaseCriteria criteria, final CaseRepository repository) {
        super.criteria = criteria;
        super.repository = repository;
    }

    @Override
    public Flux<Case> filter() {
        final Pageable pageable = PageRequest.of(super.criteria.getPage(), super.criteria.getLimit());
        final Long startsAt = super.criteria.getFrom().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        final Long endsAt = super.criteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return isFullTextSearch()
                ? filter(Flux.empty(), startsAt, endsAt, pageable)
                : super.repository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(startsAt, endsAt, pageable);
    }

    private Flux<Case> filter(final Flux<Case> current, final Long startsAt, final Long endsAt, final Pageable pageable) {
        if (pageable.getPageNumber() == super.criteria.getLimit()) {
            return current;
        }

        final Flux<Case> foundCases = super.filterByFullTextSearching(
                super.repository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(startsAt, endsAt, pageable)
        );

        return filter(
                current.mergeWith(foundCases).distinct(),
                startsAt,
                endsAt,
                pageable.next()
        );
    }

}
