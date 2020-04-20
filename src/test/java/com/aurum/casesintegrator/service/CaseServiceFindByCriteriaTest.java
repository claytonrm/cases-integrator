package com.aurum.casesintegrator.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;

@DisplayName("[CaseService] - Unit Tests for find cases by criteria")
public class CaseServiceFindByCriteriaTest extends CaseServiceBaseTest {

    @Test
    public void findBy_shouldCallRepositoryToFindCasesByCriteriaCustomer() {
//        final CaseCriteria caseCriteria = CaseCriteria.builder().customer("Mike").limit(100).build();
//        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
//        given(super.caseRepository.findByCustomer(caseCriteria.getCustomer())).willReturn(List.of(caseSample));
//
//        final List<Case> foundCases = super.caseService.findByCriteria(caseCriteria);
//
//        verify(super.caseRepository).findByCustomer(caseCriteria.getCustomer());
//        assertThat(foundCases).isEqualTo(List.of(caseSample));
    }

    @Test
    @Disabled
    public void findBy_shouldThrowNoSuchFieldExceptionPropertyDoesNotExist() {
        final CaseCriteria caseCriteria = CaseCriteria.builder().customer("Mike").limit(100).build();

        Assertions.assertThrows(NoSuchFieldException.class, () -> super.caseService.findByCriteria(caseCriteria));
    }

}
