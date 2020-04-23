package com.aurum.casesintegrator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

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

@DisplayName("[CaseService] - Unit Tests for find Cases by criteria and full text searching")
public class CaseServiceFindByCriteriaFullTextSearchTest extends CaseServiceBase {

    @Test
    public void findByCriteria_shouldFilterByDescription() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .description("Some desc").from(LocalDate.now()).to(LocalDate.now()).limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                eq(PageRequest.of(0, 100))
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldFilterByDescriptionAndTitleAndFolder() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .description("Some desc").from(LocalDate.now()).to(LocalDate.now())
                .title("sample")
                .folder("C23")
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                eq(PageRequest.of(0, 100))
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        assertThat(foundCases.collectList().block()).isEqualTo(List.of(caseSample));
    }

    @Test
    public void findByCriteria_shouldFilterByDescriptionAndTitleAndFolderReturningEmptyTitleDoesNotExist() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .description("Some desc").from(LocalDate.now()).to(LocalDate.now())
                .title("Wrong title")
                .folder("C23")
                .limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                eq(PageRequest.of(0, 100))
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        assertThat(foundCases.collectList().block()).isEmpty();
    }

    @Test
    public void findByCriteria_shouldFilterByDescriptionAndReturnEmpty() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .description("Nothing").from(LocalDate.now()).to(LocalDate.now()).limit(100).build();
        final Case caseSample = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {
        });

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                eq(PageRequest.of(0, 100))
        )).willReturn(Flux.just(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        assertThat(foundCases.collectList().block()).isEmpty();
    }

    @Test
    public void findByCriteria_shouldFilterByDescriptionAndReturnEmptyRecordHasNullDescriptionOnDatabase() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .description("Something").from(LocalDate.now()).to(LocalDate.now()).limit(100).build();
        final List<Case> caseSample = JsonUtil.fromString(FileUtil.readFile("samples/OnlyRequiredFieldsSample.json"), new TypeReference<>() {});

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                eq(PageRequest.of(0, 100))
        )).willReturn(Flux.fromIterable(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        verify(super.caseRepository).findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                DateUtil.getCurrentDateInstantZero(),
                caseCriteria.getTo().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                PageRequest.of(caseCriteria.getPage(), caseCriteria.getLimit())
        );
        assertThat(foundCases.collectList().block()).isEmpty();
    }

    @Test
    public void findByCriteria_shouldFilterByTitleCallingRepositoryUntilFindSomeOrThereIsNoMoreRecords() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .title("Est").from(LocalDate.now()).to(LocalDate.now()).limit(100).build();
        final List<Case> caseSamplePage1 = JsonUtil.fromString(FileUtil.readFile("samples/OnlyRequiredFieldsSample.json"), new TypeReference<>() {});
        final List<Case> caseSamplePage2 = JsonUtil.fromString(FileUtil.readFile("samples/BatchCasesMixedIdsNoConflictsSample.json"), new TypeReference<>() {});
        final List<Case> caseSamplePage3 = JsonUtil.fromString(FileUtil.readFile("samples/FullCasesSample.json"), new TypeReference<>() {});

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                eq(PageRequest.of(0, 100))
        )).willReturn(Flux.fromIterable(caseSamplePage1));

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                eq(PageRequest.of(1, 100))
        )).willReturn(Flux.fromIterable(caseSamplePage2));

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                eq(PageRequest.of(2, 100))
        )).willReturn(Flux.fromIterable(caseSamplePage3));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        assertThat(foundCases.collectList().block()).hasSize(1);
        assertThat(foundCases.blockFirst().getId()).isEqualTo("00caafb2-2015-4a59-b7cd-0ee9f63572d8");
    }

    @Test
    public void findByCriteria_shouldFilterByTitleCallingRepositoryAtMost100() {
        final CaseCriteria caseCriteria = CaseCriteria.builder()
                .title("Est").from(LocalDate.now()).to(LocalDate.now()).limit(100).build();
        final List<Case> caseSample = JsonUtil.fromString(FileUtil.readFile("samples/FullCasesSample.json"), new TypeReference<>() {});

        given(super.caseRepository.findByCreatedAtInstantGreaterThanEqualAndCreatedAtInstantLessThanEqualOrderByCreatedAtInstantDesc(
                anyLong(),
                anyLong(),
                any(Pageable.class)
        )).willReturn(Flux.fromIterable(caseSample));

        final Flux<Case> foundCases = super.caseService.findByCriteria(caseCriteria);

        assertThat(foundCases.collectList().block()).hasSize(1);
        assertThat(foundCases.blockFirst().getId()).isEqualTo("00caafb2-2015-4a59-b7cd-0ee9f63572d8");
    }


}
