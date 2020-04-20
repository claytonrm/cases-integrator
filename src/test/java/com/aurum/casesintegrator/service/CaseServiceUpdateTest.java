package com.aurum.casesintegrator.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.aurum.casesintegrator.domain.AccessType;
import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.util.DateUtil;

import reactor.core.publisher.Mono;

@DisplayName("[CaseService] - Unit Tests for update Cases")
public class CaseServiceUpdateTest extends CaseServiceBaseTest {

    @Test
    public void updateAllFields_shouldUpdateAllFields() {
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

}
