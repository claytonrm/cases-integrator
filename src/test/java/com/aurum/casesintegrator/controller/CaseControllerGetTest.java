package com.aurum.casesintegrator.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;

import com.aurum.casesintegrator.domain.AccessType;
import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.util.FileUtil;
import com.aurum.casesintegrator.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@DisplayName("[CaseController] - Unit Tests for GET requests on Case Controller")
public class CaseControllerGetTest extends CaseControllerBase {
    @Test
    public void findById_shouldCallServiceToFindResourceByIdAndReturnSuccessResponse() throws Exception {
        final String expectedJson = FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json");
        final Case expectedCase = JsonUtil.fromString(expectedJson, new TypeReference<>() {});
        given(super.caseService.findById(anyString())).willReturn(Mono.just(expectedCase));

        final MvcResult asyncResult = super.mockMvc
                .perform(get(TARGET_RELATIVE_PATH + "/870cd9a8-b07a-41f4-b8a6-7dcb8bec3344")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertThat(asyncResult.getAsyncResult()).isEqualTo(ResponseEntity.ok(expectedCase));

        verify(super.caseService).findById("870cd9a8-b07a-41f4-b8a6-7dcb8bec3344");
    }

    @Test
    public void findById_shouldCallServiceAndReturnStatusNotFound() throws Exception {
        given(super.caseService.findById(anyString())).willReturn(Mono.empty());

        final MvcResult asyncResult = super.mockMvc
                .perform(get(TARGET_RELATIVE_PATH + "/870cd9a8-b07a-41f4-b8a6-7dcb8bec3344")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertThat(asyncResult.getAsyncResult()).isEqualTo(ResponseEntity.notFound().build());


        verify(super.caseService).findById("870cd9a8-b07a-41f4-b8a6-7dcb8bec3344");
    }

    @Test
    public void findByCriteria_shouldCallServiceToFindBySomeCriteriaAndReturnSuccessWithResponseBody() throws Exception {
        final String expectedCasesJson = FileUtil.readFile("samples/FullCasesSample.json");
        final List<Case> foundCases = JsonUtil.fromString(expectedCasesJson, new TypeReference<>() {});
        final CaseCriteria criteria = CaseCriteria.builder()
                .accessType(AccessType.PUBLIC.name())
                .from(LocalDate.of(2020, 04, 21))
                .to(LocalDate.of(2020, 04, 21))
                .limit(100)
                .build();
        given(super.caseService.findByCriteria(criteria)).willReturn(Flux.fromIterable(foundCases));

        final MvcResult asyncResult = super.mockMvc
                .perform(get(TARGET_RELATIVE_PATH)
                        .queryParam("accessType", "PUBLIC")
                        .queryParam("from", "2020-04-21")
                        .queryParam("to", "2020-04-21"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(asyncResult.getAsyncResult()).isEqualTo(foundCases);

        verify(super.caseService).findByCriteria(criteria);
    }

    @Test
    public void findByCriteria_shouldCallServiceToFindBySomeCriteriaAndReturnSuccessWithEmptyResponse() throws Exception {
        final CaseCriteria criteria = CaseCriteria.builder()
                .accessType(AccessType.PUBLIC.name())
                .from(LocalDate.of(2020, 04, 21))
                .to(LocalDate.of(2020, 04, 21))
                .limit(100)
                .build();
        given(super.caseService.findByCriteria(criteria)).willReturn(Flux.empty());

        final MvcResult asyncResult = super.mockMvc
                .perform(get(TARGET_RELATIVE_PATH)
                        .queryParam("accessType", "PUBLIC")
                        .queryParam("from", "2020-04-21")
                        .queryParam("to", "2020-04-21"))
                    .andExpect(status().isOk())
                .andReturn();

        assertThat(asyncResult.getAsyncResult().toString()).isEqualTo("[]");

        verify(super.caseService).findByCriteria(criteria);
    }

    @Test
    public void findByCriteria_shouldCallServiceThrowingIllegalStateExceptionAccessTypeInvalidAndReturnStatusPreconditionFailed() throws Exception {
        final CaseCriteria criteria = CaseCriteria.builder()
                .accessType("PUBLICC")
                .from(LocalDate.of(2020, 04, 21))
                .to(LocalDate.of(2020, 04, 21))
                .limit(100)
                .build();
        final String expectedExceptionMessage = "Invalid Access Type param. Available options are [" + AccessType.values() + "]";
        given(super.caseService.findByCriteria(criteria)).willThrow(new IllegalStateException(expectedExceptionMessage));

        super.mockMvc.perform(get(TARGET_RELATIVE_PATH).queryParam("accessType", "PUBLICC")
                .queryParam("from", "2020-04-21")
                .queryParam("to", "2020-04-21"))
                .andExpect(status().isPreconditionFailed())
                .andExpect(jsonPath("$.messages[0]", is(expectedExceptionMessage)));
    }

}
