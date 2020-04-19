package com.aurum.casesintegrator.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.InstanceAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.util.Constants;
import com.aurum.casesintegrator.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    @Autowired
    public CaseService(final CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public List<Case> create(final List<Case> cases) throws InstanceAlreadyExistsException {
        final Set<Long> idsAlreadyFilled = cases.stream().filter(c -> c.getId() != null).map(Case::getId).collect(Collectors.toSet());
        if (idsAlreadyFilled.isEmpty()) {
            return this.saveAll(cases);
        }

        final List<Case> originalCases = new ArrayList<>(cases);
        this.removeCaseConflicts(cases);
        this.verifyAlreadyExistingCases(cases, idsAlreadyFilled);
        return this.saveAndFetchAll(cases, originalCases);
    }

    public List<Case> getExtractedCasesFrom(final String singleOrMultiple) {
        final List<Case> multipleCases = JsonUtil.fromString(singleOrMultiple, new TypeReference<>() {});
        if (multipleCases != null) {
            return multipleCases;
        }
        final Case singleCase = JsonUtil.fromString(singleOrMultiple, new TypeReference<>() {});
        return singleCase != null ? List.of(singleCase) : List.of();
    }

    public void updateAllFields(final Case newCaseData) {
        if (newCaseData.getId() == null) {
            throw new IllegalArgumentException("Field id must be filled.");
        }

        if (!this.caseRepository.findById(newCaseData.getId()).isPresent()) {
            throw new IllegalArgumentException("Case not found on database.");
        }

        this.caseRepository.save(newCaseData);
    }

    private List<Case> findAllById(Set<Long> ids) {
        final List<Case> foundCases = new ArrayList<>();
        this.caseRepository.findAllById(ids).iterator().forEachRemaining(foundCases::add);
        return foundCases;
    }

    private void removeCaseConflicts(final List<Case> cases) {
        final Set<Long> uniqueIds = new HashSet<>();
        final List<Case> casesToRemove = cases.stream().filter(c -> c.getId() != null && !uniqueIds.add(c.getId())).collect(Collectors.toList());
        cases.removeAll(casesToRemove);
    }

    private void verifyAlreadyExistingCases(final List<Case> cases, final Set<Long> idsAlreadyFilled) throws InstanceAlreadyExistsException {
        final List<Case> foundCases = this.findAllById(idsAlreadyFilled);
        cases.removeIf(c -> foundCases.stream().anyMatch(foundCase -> foundCase.getId().equals(c.getId())));
        if (cases.isEmpty() && idsAlreadyFilled.size() == Constants.SINGLE_CASE) {
            throw new InstanceAlreadyExistsException("Case already exists on database.");
        }
    }

    private List<Case> saveAndFetchAll(final List<Case> cases, final List<Case> originalCases) {
        return originalCases.stream().map(c -> cases.contains(c) ? this.caseRepository.save(c) : new Case(
                null,
                c.getFolder(),
                c.getCustomer(),
                c.getTitle(),
                c.getLabels(),
                c.getDescription(),
                c.getNotes(),
                c.getInChargeOf(),
                c.getAccessType(),
                c.getCreatedAt()
        )).collect(Collectors.toList());
    }

    private List<Case> saveAll(final List<Case> cases) {
        final List<Case> createdCases = new ArrayList<>();
        this.caseRepository.saveAll(cases).iterator().forEachRemaining(createdCases::add);
        return createdCases;
    }

}
