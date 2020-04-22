package com.aurum.casesintegrator.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.management.InstanceAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.util.Constants;
import com.aurum.casesintegrator.util.DateUtil;

import reactor.core.publisher.Flux;

@Component
public class BatchCaseService {

    private final CaseRepository caseRepository;


    @Autowired
    public BatchCaseService(final CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public Flux<Case> create(final List<Case> cases) throws InstanceAlreadyExistsException {
        final Set<String> idsAlreadyFilled = cases.stream().filter(c -> c.getId() != null).map(Case::getId).collect(Collectors.toSet());
        if (idsAlreadyFilled.isEmpty()) {
            return this.caseRepository.saveAll(generateValuesForMissingFields(cases));
        }
        final List<Case> casesNewIdGenerated = generateValuesForMissingFields(cases);
        final List<Case> originalCases = new ArrayList<>(casesNewIdGenerated);
        this.removeCaseConflicts(casesNewIdGenerated);
        this.verifyAlreadyExistingCases(casesNewIdGenerated, idsAlreadyFilled);
        return this.saveAndFetchAll(casesNewIdGenerated, originalCases);
    }

    private List<Case> generateValuesForMissingFields(final List<Case> cases) {
        return cases.stream().map(c -> {
            c.setId(c.getId() == null ? UUID.randomUUID().toString() : c.getId());
            c.setCreatedAtInstant(c.getCreatedAtInstant() == null ? DateUtil.getCurrentDateInstantZero() : c.getCreatedAtInstant());
            return c;
        }).collect(Collectors.toList());
    }

    private void removeCaseConflicts(final List<Case> cases) {
        final Set<String> uniqueIds = new HashSet<>();
        final List<Case> casesToRemove = cases.stream().filter(c -> c.getId() != null && !uniqueIds.add(c.getId())).collect(Collectors.toList());
        cases.removeAll(casesToRemove);
    }

    private void verifyAlreadyExistingCases(final List<Case> cases, final Set<String> idsAlreadyFilled) throws InstanceAlreadyExistsException {
        final Flux<Case> foundCases = this.caseRepository.findAllById(idsAlreadyFilled);
        cases.removeIf(c -> foundCases.any(foundCase -> foundCase.getId().equals(c.getId())).block());
        if (cases.isEmpty() && idsAlreadyFilled.size() == Constants.SINGLE_CASE) {
            throw new InstanceAlreadyExistsException("Case already exists on database.");
        }
    }

    private Flux<Case> saveAndFetchAll(final List<Case> cases, final List<Case> originalCases) {
        return Flux.fromIterable(
                originalCases.stream().map(c -> cases.contains(c) ? this.caseRepository.save(c).block() :
                        new Case(
                                null,
                                c.getFolder(),
                                c.getCustomer(),
                                c.getTitle(),
                                c.getLabels(),
                                c.getDescription(),
                                c.getNotes(),
                                c.getInChargeOf(),
                                c.getAccessType(),
                                c.getCreatedAtInstant()
                        )).collect(Collectors.toList())
        );
    }

}
