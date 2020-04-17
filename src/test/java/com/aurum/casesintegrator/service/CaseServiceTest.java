package com.aurum.casesintegrator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.aurum.casesintegrator.domain.AccessType;
import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.repository.CaseRepository;

@SpringBootTest
@DisplayName("[CaseService] - Test cases for Legal Cases Services")
public class CaseServiceTest {

    @Autowired
    private CaseService caseService;

    @MockBean
    private CaseRepository caseRepository;

    @Test
    public void create_shouldCallRepositoryToCreateASingleCase() throws InstanceAlreadyExistsException {
        /* Given */
        final List<Case> cases = List.of(new Case(null,
                "O34398",
                "Clayton",
                "Some case",
                Set.of("important"),
                "Some description",
                "Is someone getting the best of you...",
                "SRV",
                AccessType.PUBLIC,
                LocalDateTime.now())
        );
        final Case singleCase = cases.stream().findFirst().get();

        given(this.caseRepository.saveAll(cases)).willReturn(
                List.of(
                        new Case(1L,
                                singleCase.getFolder(),
                                singleCase.getCustomer(),
                                singleCase.getTitle(),
                                singleCase.getLabels(),
                                singleCase.getDescription(),
                                singleCase.getNotes(),
                                singleCase.getInChargeOf(),
                                singleCase.getAccessType(),
                                singleCase.getCreatedAt()
                        )
                )
        );

        /* When */
        final List<Case> createdCases = this.caseService.create(cases);

        /* Then */
        verify(this.caseRepository).saveAll(cases);
        assertThat(createdCases).isNotNull();
        assertThat(createdCases.stream().findFirst().get().getId()).isNotNull();
    }

    @Test
    public void create_shouldCallRepositoryAndThrowConflictErrorIdAlreadyExists() {
        final long sameId = 1L;
        final List<Case> duplicatedCases = getMockedDuplicatedCases(sameId);
        given(this.caseRepository.findAllById(Set.of(sameId))).willReturn(Arrays.asList(duplicatedCases.get(0)));

        assertThrows(InstanceAlreadyExistsException.class, () -> this.caseService.create(duplicatedCases));
    }

    @Test
    public void create_shouldCallRepositoryToSaveOnlyOneCase() throws InstanceAlreadyExistsException {
        /* Given */
        final long sameId = 1L;
        final int expectedCasesNumber = getMockedDuplicatedCases(sameId).size();
        final List<Case> duplicatedCases = getMockedDuplicatedCases(sameId);
        final Case originalCase = duplicatedCases.get(0);
        given(this.caseRepository.findAllById(Set.of(sameId))).willReturn(List.of());
        given(this.caseRepository.save(originalCase)).willReturn(originalCase);

        /* When */
        final List<Case> createdCases = this.caseService.create(duplicatedCases);

        /* Then */
        verify(this.caseRepository).save(originalCase);
        assertThat(createdCases).hasSize(expectedCasesNumber);
        assertThat(createdCases).isNotNull();
        assertThat(createdCases.stream().findFirst().get().getId()).isNotNull();
    }

    private ArrayList<Case> getMockedDuplicatedCases(long sameId) {
        return new ArrayList<>(List.of(
                new Case(1L,
                        "O34398",
                        "Clayton",
                        "Some case",
                        Set.of("important"),
                        "Some description",
                        "Is someone getting the best of you...",
                        "SRV",
                        AccessType.PUBLIC,
                        LocalDateTime.now()
                ),
                new Case(sameId,
                        "O34398",
                        "Ribeiro",
                        "Some case",
                        Set.of("black"),
                        "Some description",
                        "Is someone getting the best of you...",
                        "SRV",
                        AccessType.PUBLIC,
                        LocalDateTime.now()
                ),
                new Case(sameId,
                        "O34398",
                        "Mendonça",
                        "Some case",
                        Set.of("black"),
                        "Some description",
                        "Is someone getting the best of you...",
                        "SRV",
                        AccessType.PUBLIC,
                        LocalDateTime.now()
                )
        ));
    }

}
