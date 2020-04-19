package com.aurum.casesintegrator.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.management.InstanceAlreadyExistsException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.CaseService;
import com.aurum.casesintegrator.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CaseController.class)
@DisplayName("[CaseController] - Unit Tests for Case Controller")
public class CaseControllerTest {

    private static final String TARGET_RELATIVE_PATH = "/v1/cases";
    private static final int NO_INTERACTION = 0;

    private static ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaseService caseService;

    @MockBean
    private CaseRepository caseRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        mapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(this.caseService, this.caseRepository);
    }

    @Test
    public void create_shouldCallServiceToCreateASingleCaseAndReturnStatusCreatedWithResponseBody() throws Exception {
        final String jsonRequest = FileUtil.readFile("LegalCaseFullSample.json");
        given(this.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(mapper.readValue(jsonRequest, Case.class)));

        final Case expectedCaseFromService = mapper.readValue(jsonRequest, Case.class);
        expectedCaseFromService.setId(1L);
        given(this.caseService.create(Mockito.any())).willReturn(List.of(expectedCaseFromService));

        this.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(HttpStatus.CREATED.name())))
                .andExpect(jsonPath("$.uri", is("http://localhost/v1/cases/1")));

        verify(this.caseService).create(Mockito.any());
    }

    @Test
    public void create_shouldValidateEmptyRequestBodyAndReturnStatusBadRequest() throws Exception {
        this.mockMvc.perform(post(TARGET_RELATIVE_PATH).content("").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(this.caseService);
    }

    @Test
    public void create_shouldValidateRequiredParamCustomerAndReturnStatusBadRequest() throws Exception {
        final String jsonRequest = FileUtil.readFile("LegalCaseMissingCustomerSample.json");
        given(this.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(mapper.readValue(jsonRequest, Case.class)));

        this.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", contains("Property \"customer\" must not be blank.")));

        verify(this.caseService, times(NO_INTERACTION)).create(Mockito.anyList());
    }

    @Test
    public void create_shouldValidateAllRequiredParamsAndReturnStatusBadRequest() throws Exception {
        final String jsonRequest = FileUtil.readFile("LegalCaseMissingAllRequiredFieldsSample.json");
        given(this.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(mapper.readValue(jsonRequest, Case.class)));

        this.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "Property \"customer\" must not be blank.",
                        "Property \"title\" must not be blank.",
                        "Property \"inChargeOf\" must not be blank.",
                        "Property \"createdAt\" must not be null."
                )));

        verify(this.caseService, times(NO_INTERACTION)).create(anyList());
    }

    @Test
    public void create_shouldValidateMaxLengthOnFolderPropertyAndReturnStatusBadRequest() throws Exception {
        final String jsonRequest = FileUtil.readFile("LegalCaseExceededFolderMaxLengthSample.json");
        given(this.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(mapper.readValue(jsonRequest, Case.class)));

        this.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", contains("Property \"folder\" must not exceed max length 40.")));

        verify(this.caseService, times(NO_INTERACTION)).create(anyList());
    }

    @Test
    public void create_shouldCallServiceToCreateLoadsOfCasesAndReturnStatusMultipleStatus() throws Exception {
        final String jsonRequest = FileUtil.readFile("BatchCasesReducedSample.json");
        final List<Case> expectedCasesFromService = mapper.readValue(jsonRequest, new TypeReference<>() {
        });
        given(this.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(expectedCasesFromService);
        given(this.caseService.create(expectedCasesFromService)).willReturn(expectedCasesFromService.stream()
                .peek(singleCase -> singleCase.setId(new Random().nextLong()))
                .collect(Collectors.toList())
        );

        this.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isMultiStatus());
    }

    @Test
    public void create_shouldCallServiceToCreateLoadsOfCasesAndReturnStatusMultipleStatusContainingConflicts() throws Exception {
        final String jsonRequest = FileUtil.readFile("BatchCasesMixedIdsSample.json");
        final List<Case> expectedCasesFromService = mapper.readValue(jsonRequest, new TypeReference<>() {});
        given(this.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(expectedCasesFromService);
        final List<Case> expectedAfterSaving = mapper.readValue(FileUtil.readFile("BatchCasesMixedIdsNoConflictsSample.json"), new TypeReference<>() {});
        given(this.caseService.create(expectedCasesFromService)).willReturn(expectedAfterSaving);

        this.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$.[0].status", is(HttpStatus.CREATED.name())))
                .andExpect(jsonPath("$.[1].status", is(HttpStatus.CREATED.name())))
                .andExpect(jsonPath("$.[2].status", is(HttpStatus.CONFLICT.name())))
                .andExpect(jsonPath("$.[3].status", is(HttpStatus.CREATED.name())))
                .andExpect(jsonPath("$.[4].status", is(HttpStatus.CONFLICT.name())));
    }

    @Test
    public void create_shouldCallServiceThrowingAnInstanceAlreadyExistsExceptionAndReturnConflictStatusCode() throws Exception {
        final String jsonRequest = FileUtil.readFile("LegalCaseIdAlreadyFilledSample.json");
        final Case expectedCaseFromService = mapper.readValue(jsonRequest, new TypeReference<>() {});
        given(this.caseService.getExtractedCasesFrom(jsonRequest)).willReturn(List.of(expectedCaseFromService));
        given(this.caseService.create(List.of(expectedCaseFromService))).willThrow(new InstanceAlreadyExistsException("Case already exists on database."));

        this.mockMvc.perform(post(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.messages[0]", is("Case already exists on database.")));
    }

    @Test
    public void update_shouldCallServiceToUpdateAllCaseFields() throws Exception {
        final String jsonRequest = FileUtil.readFile("LegalCaseIdAlreadyFilledSample.json");
        this.mockMvc.perform(put(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    public void update_shouldReturnBadRequestRequiredFieldsAreEmpty() throws Exception {
        final String jsonRequest = "{\"id\": 123456, \"customer\": \"Mike\"}";
        this.mockMvc.perform(put(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_shouldCallServiceThrowingIllegalArgumentExceptionAndReturnBadRequestStatus() throws Exception {
        final String jsonRequest = FileUtil.readFile("LegalCaseIdAlreadyFilledSample.json");
        final Case caseToUpdate = mapper.readValue(jsonRequest, new TypeReference<>(){});
        doThrow(new IllegalArgumentException("Field id must be filled.")).when(this.caseService).updateAllFields(caseToUpdate);

        this.mockMvc.perform(put(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

}
