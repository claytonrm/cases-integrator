package com.aurum.casesintegrator.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import reactor.core.publisher.Flux;

@DisplayName("[CaseController] - Unit Tests for POST requests on Case Controller")
public class CaseControllerPostTest extends CaseControllerBase {

    @Test
    public void create_shouldCallServiceToCreateASingleCaseAndReturnStatusCreatedWithResponseBody() throws Exception {
        final String jsonRequest = FileUtil.readFile("samples/LegalCaseFullSample.json");
        given(super.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(mapper.readValue(jsonRequest, Case.class)));

        final Case expectedCaseFromService = mapper.readValue(jsonRequest, Case.class);
        expectedCaseFromService.setId("1");
        given(super.caseService.create(Mockito.any())).willReturn(Flux.just(expectedCaseFromService));

        super.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.status", is(HttpStatus.CREATED.name())))
                .andExpect(jsonPath("$.uri", is("http://localhost/v1/cases/1")));

        verify(super.caseService).create(Mockito.any());
    }

    @Test
    public void create_shouldValidateEmptyRequestBodyAndReturnStatusBadRequest() throws Exception {
        super.mockMvc.perform(post(TARGET_RELATIVE_PATH).content("").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(super.caseService);
    }

    @Test
    public void create_shouldValidateRequiredParamCustomerAndReturnStatusBadRequest() throws Exception {
        final String jsonRequest = FileUtil.readFile("samples/LegalCaseMissingCustomerSample.json");
        given(super.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(mapper.readValue(jsonRequest, Case.class)));

        super.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", contains("Property \"customer\" must not be blank.")));

        verify(super.caseService, times(NO_INTERACTION)).create(Mockito.anyList());
    }

    @Test
    public void create_shouldValidateAllRequiredParamsAndReturnStatusBadRequest() throws Exception {
        final String jsonRequest = FileUtil.readFile("samples/LegalCaseMissingAllRequiredFieldsSample.json");
        given(super.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(mapper.readValue(jsonRequest, Case.class)));

        super.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "Property \"customer\" must not be blank.",
                        "Property \"title\" must not be blank.",
                        "Property \"inChargeOf\" must not be blank."
                )));

        verify(super.caseService, times(NO_INTERACTION)).create(anyList());
    }

    @Test
    public void create_shouldValidateMaxLengthOnFolderPropertyAndReturnStatusBadRequest() throws Exception {
        final String jsonRequest = FileUtil.readFile("samples/LegalCaseExceededFolderMaxLengthSample.json");
        given(super.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(mapper.readValue(jsonRequest, Case.class)));

        super.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", contains("Property \"folder\" must not exceed max length 40.")));

        verify(super.caseService, times(NO_INTERACTION)).create(anyList());
    }

    @Test
    public void create_shouldCallServiceToCreateLoadsOfCasesAndReturnStatusMultipleStatus() throws Exception {
        final String jsonRequest = FileUtil.readFile("samples/BatchCasesReducedSample.json");
        final List<Case> expectedCasesFromService = mapper.readValue(jsonRequest, new TypeReference<>() {});
        given(super.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(expectedCasesFromService);
        given(super.caseService.create(expectedCasesFromService)).willReturn(Flux.fromIterable(
                expectedCasesFromService.stream().peek(singleCase -> singleCase.setId(String.valueOf(new Random().nextLong()))).collect(Collectors.toList())
        ));

        super.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isMultiStatus());
    }

    @Test
    public void create_shouldCallServiceToCreateLoadsOfCasesAndReturnStatusMultipleStatusContainingConflicts() throws Exception {
        final String jsonRequest = FileUtil.readFile("samples/BatchCasesMixedIdsSample.json");
        final List<Case> expectedCasesFromService = mapper.readValue(jsonRequest, new TypeReference<>() {});
        given(super.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(expectedCasesFromService);
        final List<Case> expectedAfterSaving = mapper.readValue(FileUtil.readFile("samples/BatchCasesMixedIdsNoConflictsSample.json"), new TypeReference<>() {});
        given(super.caseService.create(expectedCasesFromService)).willReturn(Flux.fromIterable(expectedAfterSaving));

        super.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$.[0].status", is(HttpStatus.CREATED.name())))
                .andExpect(jsonPath("$.[1].status", is(HttpStatus.CREATED.name())))
                .andExpect(jsonPath("$.[2].status", is(HttpStatus.CONFLICT.name())))
                .andExpect(jsonPath("$.[3].status", is(HttpStatus.CREATED.name())))
                .andExpect(jsonPath("$.[4].status", is(HttpStatus.CONFLICT.name())));
    }

    @Test
    public void create_shouldCallServiceThrowingAnInstanceAlreadyExistsExceptionAndReturnConflictStatusCode() throws Exception {
        final String jsonRequest = FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json");
        final Case expectedCaseFromService = mapper.readValue(jsonRequest, new TypeReference<>() {
        });
        given(super.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(expectedCaseFromService));
        given(super.caseService.create(List.of(expectedCaseFromService))).willThrow(new InstanceAlreadyExistsException("Case already exists on database."));

        super.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.messages[0]", is("Case already exists on database.")));
    }

}
