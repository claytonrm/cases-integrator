package com.aurum.casesintegrator.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.management.InstanceAlreadyExistsException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.ResourceCreatedResponse;
import com.aurum.casesintegrator.service.CaseService;
import com.aurum.casesintegrator.util.Constants;
import com.aurum.casesintegrator.validation.constraint.ValidLegalCase;

@Validated
@RestController
@RequestMapping("/v1/cases")
public class CaseController {

    private static final String RELATIVE_PATH_RESOURCE_ID = "/v1/cases/{id}";
    private final CaseService caseService;

    @Autowired
    public CaseController(final CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@ValidLegalCase @RequestBody final String singleOrMultipleCase, final UriComponentsBuilder uriBuilder) throws InstanceAlreadyExistsException {
        final List<Case> casesToCreate = this.caseService.getExtractedCasesFrom(singleOrMultipleCase);
        final List<Case> createdCases = this.caseService.create(casesToCreate);

        if (createdCases.stream().count() == Constants.SINGLE_CASE) {
            return createSingleStatusResponseCreated(uriBuilder, createdCases);
        }
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(createMultipleStatusBody(createdCases, uriBuilder));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> update(@Valid @RequestBody final Case singleCase) {
        this.caseService.updateAllFields(singleCase);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Object> createSingleStatusResponseCreated(UriComponentsBuilder uriBuilder, List<Case> createdCases) {
        final Case singleCase = createdCases.stream().findFirst().get();
        final UriComponents uriComponent = uriBuilder.path(RELATIVE_PATH_RESOURCE_ID).buildAndExpand(singleCase.getId());
        return ResponseEntity.created(uriComponent.toUri()).body(
                ResourceCreatedResponse.builder()
                        .id(singleCase.getId())
                        .createdAt(singleCase.getCreatedAt())
                        .status(HttpStatus.CREATED)
                        .uri(uriComponent.toUriString())
                        .build()
        );
    }

    private List<ResourceCreatedResponse> createMultipleStatusBody(final List<Case> createdCases, final UriComponentsBuilder uriBuilder) {
        return createdCases.stream().map(singleCase -> {
            final String uri = uriBuilder.cloneBuilder().path(RELATIVE_PATH_RESOURCE_ID).buildAndExpand(singleCase.getId()).toUriString();
            final boolean isIdConflicted = singleCase.getId() == null;
            return ResourceCreatedResponse.builder()
                    .id(singleCase.getId())
                    .createdAt(singleCase.getCreatedAt())
                    .status(isIdConflicted ? HttpStatus.CONFLICT : HttpStatus.CREATED)
                    .uri(isIdConflicted ? null : uri)
                    .build();
        }).collect(Collectors.toList());
    }

}
