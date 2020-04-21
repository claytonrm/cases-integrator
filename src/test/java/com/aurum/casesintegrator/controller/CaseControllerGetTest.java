package com.aurum.casesintegrator.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.aurum.casesintegrator.domain.AccessType;
import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.domain.CaseCriteria;
import com.aurum.casesintegrator.util.FileUtil;
import com.aurum.casesintegrator.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@DisplayName("[CaseController] - Unit Tests for GET requests on Case Controller")
public class CaseControllerGetTest extends CaseControllerBaseTest {
    @Test
    public void findById_shouldCallServiceToFindResourceByIdAndReturnSuccessResponse() throws Exception {
        final String expectedJson = FileUtil.readFile("LegalCaseIdAlreadyFilledSample.json");
        final Case expectedCase = JsonUtil.fromString(expectedJson, new TypeReference<>() {
        });
        given(super.caseService.findById(anyString())).willReturn(Mono.just(expectedCase));

        super.mockMvc.perform(get(TARGET_RELATIVE_PATH + "/870cd9a8-b07a-41f4-b8a6-7dcb8bec3344"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(super.caseService).findById("870cd9a8-b07a-41f4-b8a6-7dcb8bec3344");
    }

    @Test
    public void findById_shouldCallServiceThrowingInstanceNotFoundExceptionAndReturnStatusNotFound() throws Exception {
        given(super.caseService.findById(anyString())).willThrow(new InstanceNotFoundException("Resource 870cd9a8-b07a-41f4-b8a6-7dcb8bec3344 does not exist."));

        super.mockMvc.perform(get(TARGET_RELATIVE_PATH + "/870cd9a8-b07a-41f4-b8a6-7dcb8bec3344"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]", is("Resource 870cd9a8-b07a-41f4-b8a6-7dcb8bec3344 does not exist.")));

        verify(super.caseService).findById("870cd9a8-b07a-41f4-b8a6-7dcb8bec3344");
    }

    @Test
    public void findByCriteria_shouldCallServiceToFindBySomeCriteriaAndReturnSuccessWithResponseBody() throws Exception {
        final String expectedCasesJson = FileUtil.readFile("FullCasesSample.json");
        final List<Case> foundCases = JsonUtil.fromString(expectedCasesJson, new TypeReference<>() {});
        final CaseCriteria criteria = CaseCriteria.builder()
                .accessType(AccessType.PUBLIC.name())
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100)
                .build();
        given(super.caseService.findByCriteria(criteria)).willReturn(Flux.fromIterable(foundCases));

        super.mockMvc.perform(get(TARGET_RELATIVE_PATH)
                .queryParam("accessType", "PUBLIC")
                .queryParam("from", "2020-04-21")
                .queryParam("to", "2020-04-21"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedCasesJson));

        verify(super.caseService).findByCriteria(criteria);
    }

    @Test
    public void findByCriteria_shouldCallServiceToFindBySomeCriteriaAndReturnSuccessWithEmptyResponse() throws Exception {
        final CaseCriteria criteria = CaseCriteria.builder()
                .accessType(AccessType.PUBLIC.name())
                .from(LocalDate.now())
                .to(LocalDate.now())
                .limit(100)
                .build();
        given(super.caseService.findByCriteria(criteria)).willReturn(Flux.empty());

        super.mockMvc.perform(get(TARGET_RELATIVE_PATH)
                .queryParam("accessType", "PUBLIC")
                .queryParam("from", "2020-04-21")
                .queryParam("to", "2020-04-21"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(super.caseService).findByCriteria(criteria);
    }

    @Test
    public void findByCriteria_shouldCallServiceThrowingIllegalStateExceptionAccessTypeInvalidAndReturnStatusPreconditionFailed() throws Exception {
        final CaseCriteria criteria = CaseCriteria.builder()
                .accessType("PUBLICC")
                .from(LocalDate.now())
                .to(LocalDate.now())
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
