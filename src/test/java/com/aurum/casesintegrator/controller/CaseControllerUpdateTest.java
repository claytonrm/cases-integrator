package com.aurum.casesintegrator.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;

@DisplayName("[CaseController] - Unit Tests for PUT requests on Case Controller")
public class CaseControllerUpdateTest extends CaseControllerBaseTest {

    @Test
    public void update_shouldCallServiceToUpdateAllCaseFields() throws Exception {
        final String jsonRequest = FileUtil.readFile("samples/LegalCaseIdAlreadyFilledSample.json");
        super.mockMvc.perform(put(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    public void update_shouldReturnBadRequestRequiredFieldsAreEmpty() throws Exception {
        final String jsonRequest = "{\"id\": 123456, \"customer\": \"Mike\"}";
        super.mockMvc.perform(put(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_shouldCallServiceThrowingIllegalArgumentExceptionAndReturnBadRequestStatus() throws Exception {
        final String jsonRequest = FileUtil.readFile("./samples/LegalCaseIdAlreadyFilledSample.json");
        final Case caseToUpdate = mapper.readValue(jsonRequest, new TypeReference<>() {});
        doThrow(new IllegalArgumentException("Field id must be filled.")).when(super.caseService).updateAllFields(caseToUpdate);

        super.mockMvc.perform(put(TARGET_RELATIVE_PATH).content(jsonRequest).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

}
