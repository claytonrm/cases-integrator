package com.aurum.casesintegrator.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.domain.ResourceCreatedResponse;
import com.aurum.casesintegrator.service.CaseService;
import com.aurum.casesintegrator.util.Constants;
import com.aurum.casesintegrator.validation.constraint.ValidLegalCase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Api(tags = "Cases", value = "Resources for cases endpoints")
@Validated
@RestController
@RequestMapping("/v1/cases")
public class CaseController {

    @Value("${fetch.pages.limit}")
    private int pageLimit;

    @Value("${fetch.months.limit}")
    private int monthsLimit;

    private final CaseService caseService;

    @Autowired
    public CaseController(final CaseService caseService) {
        this.caseService = caseService;
    }

    @ApiOperation(value = "Create a single or multiple case(s)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@ValidLegalCase @RequestBody final String singleOrMultipleCase, final UriComponentsBuilder uriBuilder) throws InstanceAlreadyExistsException {
        final List<Case> casesToCreate = this.caseService.getExtractedCasesFrom(singleOrMultipleCase);
        final Flux<Case> createdCases = this.caseService.create(casesToCreate);

        if (createdCases.toStream().count() == Constants.SINGLE_CASE) {
            return createSingleStatusResponseCreated(uriBuilder, createdCases);
        }
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(createMultipleStatusBody(createdCases, uriBuilder));
    }

    @ApiOperation(value = "Update case fields")
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> update(@Valid @RequestBody final Case singleCase) {
        this.caseService.updateAllFields(singleCase);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Get case by resource id")
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Case>> findById(@PathVariable String id) {
        return this.caseService.findById(id)
                .map(c -> ResponseEntity.ok(c))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @ApiOperation(value = "Get case(s) by specific criteria")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Case> findByCriteria(final CaseCriteria caseCriteria) {
        fillMissingRequiredFields(caseCriteria);
        return this.caseService.findByCriteria(caseCriteria);
    }

    private void fillMissingRequiredFields(CaseCriteria caseCriteria) {
        caseCriteria.setLimit(caseCriteria.getLimit() == null ? pageLimit : caseCriteria.getLimit());
        caseCriteria.setFrom(caseCriteria.getFrom() == null ? LocalDate.now().minusMonths(monthsLimit) : caseCriteria.getFrom());
        caseCriteria.setTo(caseCriteria.getTo() == null ? LocalDate.now().plusMonths(monthsLimit) : caseCriteria.getTo());
    }

    private ResponseEntity<Object> createSingleStatusResponseCreated(UriComponentsBuilder uriBuilder, Flux<Case> createdCases) {
        final Case singleCase = createdCases.blockFirst();
        final UriComponents uriComponent = generateUriResource(singleCase.getId(), uriBuilder);
        return ResponseEntity.created(uriComponent.toUri()).body(
                ResourceCreatedResponse.builder()
                        .id(singleCase.getId())
                        .createdAt(LocalDateTime.now())
                        .status(HttpStatus.CREATED)
                        .uri(uriComponent.toUriString())
                        .build()
        );
    }

    private List<ResourceCreatedResponse> createMultipleStatusBody(final Flux<Case> createdCases, final UriComponentsBuilder uriBuilder) {
        return createdCases.map(singleCase -> {
            final String uri = generateUriResource(singleCase.getId(), uriBuilder).toUriString();
            final boolean isIdConflicted = singleCase.getId() == null;
            return ResourceCreatedResponse.builder()
                    .id(singleCase.getId())
                    .createdAt(LocalDateTime.now())
                    .status(isIdConflicted ? HttpStatus.CONFLICT : HttpStatus.CREATED)
                    .uri(isIdConflicted ? null : uri)
                    .build();
        }).collectList().block();
    }

    private UriComponents generateUriResource(final String id, final UriComponentsBuilder uriComponentsBuilder) {
        return uriComponentsBuilder.cloneBuilder().path("/v1/cases/{id}").buildAndExpand(id);
    }

}
