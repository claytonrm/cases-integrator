package com.aurum.casesintegrator.service.strategy;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.service.strategy.factory.FilterCriteria;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class Criteria {

    private FilterCriteria strategy;

    public Flux<Case> filter() {
        return strategy.filter();
    }

}
