package com.aurum.casesintegrator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.aurum.casesintegrator.domain.AccessType;
import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.util.DateUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@DisplayName("[CaseService] - Unit Tests for create Cases")
public class CaseServiceCreateTest extends CaseServiceBaseTest {

    @Test
    public void create_shouldCallRepositoryToCreateASingleCase() throws InstanceAlreadyExistsException {
        /* Given */
        final List<Case> cases = List.of(new Case(null,
                "O34398",
                "Clayton",
                "Some case",
                List.of("important"),
                "Some description",
                "Is someone getting the best of you...",
                "SRV",
                AccessType.PUBLIC,
                DateUtil.getCurrentDateInstantZero())
        );
        final Case singleCase = cases.stream().findFirst().get();

        given(super.caseRepository.saveAll(cases)).willReturn(
                Flux.just(
                        new Case("1",
                                singleCase.getFolder(),
                                singleCase.getCustomer(),
                                singleCase.getTitle(),
                                singleCase.getLabels(),
                                singleCase.getDescription(),
                                singleCase.getNotes(),
                                singleCase.getInChargeOf(),
                                singleCase.getAccessType(),
                                singleCase.getCreatedAtInstant()
                        )
                )
        );

        /* When */
        final Flux<Case> createdCases = super.caseService.create(cases);

        /* Then */
        verify(super.caseRepository).saveAll(cases);
        assertThat(createdCases).isNotNull();
        assertThat(createdCases.blockFirst().getId()).isNotNull();
    }

    @Test
    public void create_shouldCallRepositoryAndThrowConflictErrorIdAlreadyExists() {
        final String sameId = "1";
        final List<Case> duplicatedCases = getMockedDuplicatedCases(sameId);
        given(super.caseRepository.findAllById(Set.of(sameId))).willReturn(Flux.just(duplicatedCases.get(0)));

        assertThrows(InstanceAlreadyExistsException.class, () -> super.caseService.create(duplicatedCases));
    }

    @Test
    public void create_shouldCallRepositoryToSaveOnlyOneCase() throws InstanceAlreadyExistsException {
        /* Given */
        final String sameId = "1";
        final int expectedCasesNumber = getMockedDuplicatedCases(sameId).size();
        final List<Case> duplicatedCases = getMockedDuplicatedCases(sameId);
        final Case originalCase = duplicatedCases.get(0);
        given(super.caseRepository.findAllById(Set.of(sameId))).willReturn(Flux.empty());
        given(super.caseRepository.save(originalCase)).willReturn(Mono.just(originalCase));

        /* When */
        final Flux<Case> createdCases = super.caseService.create(duplicatedCases);

        /* Then */
        verify(super.caseRepository).save(originalCase);
        assertThat(createdCases.toStream()).hasSize(expectedCasesNumber);
        assertThat(createdCases).isNotNull();
        assertThat(createdCases.blockFirst().getId()).isNotNull();
    }

    @Test
    public void updateAllFields_shouldCallRepositoryToUpdateAllFields() {
        /* Given */
        final Case caseToUpdate = new Case("1",
                "O34398",
                "Clayton",
                "Some case",
                List.of("important"),
                "Some description",
                "Is someone getting the best of you...",
                "SRV",
                AccessType.PUBLIC,
                DateUtil.getCurrentDateInstantZero()
        );
        given(super.caseRepository.findById(caseToUpdate.getId())).willReturn(Mono.just(caseToUpdate));

        /* When */
        super.caseService.updateAllFields(caseToUpdate);

        /* Then */
        verify(super.caseRepository).save(caseToUpdate);
    }

    @Test
    public void updateAllFields_shouldThrowAnIllegalArgumentExceptionCaseNotExists() {
        final Case caseToUpdate = new Case("1",
                "O34398",
                "Clayton",
                "Some case",
                List.of("important"),
                "Some description",
                "Is someone getting the best of you...",
                "SRV",
                AccessType.PUBLIC,
                DateUtil.getCurrentDateInstantZero()
        );
        given(super.caseRepository.findById(caseToUpdate.getId())).willReturn(Mono.empty());

        assertThrows(IllegalArgumentException.class, () -> super.caseService.updateAllFields(caseToUpdate));
    }

    private ArrayList<Case> getMockedDuplicatedCases(final String sameId) {
        return new ArrayList<>(List.of(
                new Case(sameId,
                        "O34398",
                        "Clayton",
                        "Some case",
                        List.of("important"),
                        "Some description",
                        "Is someone getting the best of you...",
                        "SRV",
                        AccessType.PUBLIC,
                        DateUtil.getCurrentDateInstantZero()
                ),
                new Case(sameId,
                        "O34398",
                        "Ribeiro",
                        "Some case",
                        List.of("black"),
                        "Some description",
                        "Is someone getting the best of you...",
                        "SRV",
                        AccessType.PUBLIC,
                        DateUtil.getCurrentDateInstantZero()
                ),
                new Case(sameId,
                        "O34398",
                        "Mendonça",
                        "Some case",
                        List.of("black"),
                        "Some description",
                        "Is someone getting the best of you...",
                        "SRV",
                        AccessType.PUBLIC,
                        DateUtil.getCurrentDateInstantZero()
                )
        ));
    }

}
