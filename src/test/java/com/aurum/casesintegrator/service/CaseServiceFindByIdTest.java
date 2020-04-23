package com.aurum.casesintegrator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.util.FileUtil;
import com.aurum.casesintegrator.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import reactor.core.publisher.Mono;

@DisplayName("[CaseService] - Unit Tests for find Case by id")
public class CaseServiceFindByIdTest extends CaseServiceBase {

    @Test
    public void findById_shouldCallRepositoryToFindCaseById() {
        final String targetId = "870cd9a8-b07a-41f4-b8a6-7dcb8bec3344";
        final Case expectedCase = JsonUtil.fromString(FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json"), new TypeReference<>() {});
        given(super.caseRepository.findById(targetId)).willReturn(Mono.just(expectedCase));

        final Mono<Case> foundCase = super.caseService.findById(targetId);

        assertThat(foundCase.block()).isEqualTo(expectedCase);
    }

}
