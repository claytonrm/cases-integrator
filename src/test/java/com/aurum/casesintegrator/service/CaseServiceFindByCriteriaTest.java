package com.aurum.casesintegrator.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.util.DateUtil;
import com.aurum.casesintegrator.util.FileUtil;
import com.aurum.casesintegrator.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import reactor.core.publisher.Flux;

@DisplayName("[CaseService] - Unit Tests for find Cases by criteria")
public class CaseServiceFindByCriteriaTest extends CaseServiceBase {

    @Test
    public void findByCriteria_shouldCallRepositoryToFindByCriteriaCreationDateAndCustomer() {
        final CaseCriteria caseCriteria = CaseCriteria.builder().customer("Mike").from(LocalDate.now()).to(LocalDate.now()).limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findByCustomerAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                anyString(),
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCustomerAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                caseCriteria.getCustomer(),
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        org.assertj.core.api.Assertions.assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldCallRepositoryToFindByCriteriaCreationDateCustomerAndLabels() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .customer("Mike")
                .labels(List.of("important"))
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findByCustomerAndLabelsContainingAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                anyString(),
                anyList(),
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCustomerAndLabelsContainingAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                caseCriteria.getCustomer(),
                caseCriteria.getLabels(),
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        org.assertj.core.api.Assertions.assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldCallRepositoryToFindByCriteriaCreationDateCustomerAndAccessType() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .customer("Mike")
                .accessType("PRIVATE")
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findByCustomerAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                anyString(),
                anyString(),
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCustomerAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                caseCriteria.getCustomer(),
                caseCriteria.getAccessType(),
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        org.assertj.core.api.Assertions.assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldCallRepositoryToFindByCriteriaCreationDateLabelsAndAccessType() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .labels(List.of("important"))
                .accessType("PUBLIC")
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findByLabelsContainingAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                anyList(),
                anyString(),
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByLabelsContainingAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                caseCriteria.getLabels(),
                caseCriteria.getAccessType(),
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        org.assertj.core.api.Assertions.assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldCallRepositoryToFindByCriteriaCreationDateLabels() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .labels(List.of("important"))
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findByLabelsContainingAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                anyList(),
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByLabelsContainingAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                caseCriteria.getLabels(),
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        org.assertj.core.api.Assertions.assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldCallRepositoryToFindByCriteriaCreationDateAccessType() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .accessType("PUBLIC")
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findByAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                anyString(),
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                caseCriteria.getAccessType(),
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        org.assertj.core.api.Assertions.assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldThrowAnIllegalStateExceptionAccessTypeIsInvalid() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .accessType("PRIVATEEEE")
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});

        Assertions.assertThrows(IllegalStateException.class, () -> super.caseService.findByCriteria(caseCriteria));

        verify(super.caseRepository, times(0)).findByAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                caseCriteria.getAccessType(),
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
    }

    @Test
    public void findByCriteria_shouldCallRepositoryToFindByCriteriaCreationDate() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        org.assertj.core.api.Assertions.assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldCallRepositoryToFindByCriteriaFullCompositeIndexSearching() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .customer("Mike")
                .labels(List.of("important"))
                .accessType("PUBLIC")
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findByCustomerAndLabelsContainingAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                anyString(),
                anyList(),
                anyString(),
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCustomerAndLabelsContainingAndAccessTypeAndCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqual(
                caseCriteria.getCustomer(),
                caseCriteria.getLabels(),
                caseCriteria.getAccessType(),
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        org.assertj.core.api.Assertions.assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

}
