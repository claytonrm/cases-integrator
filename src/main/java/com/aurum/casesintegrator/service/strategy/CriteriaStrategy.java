package com.aurum.casesintegrator.service.strategy;

import com.aurum.casesintegrator.domain.Case;

import reactor.core.publisher.Flux;

public interface CriteriaStrategy {

    Flux<Case> filter();

}
