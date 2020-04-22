package com.aurum.casesintegrator.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aurum.casesintegrator.repository.CaseRepository;
import com.aurum.casesintegrator.service.CaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CaseController.class)
public class CaseControllerBase {

    protected static final String TARGET_RELATIVE_PATH = "/v1/cases";
    protected static final int NO_INTERACTION = 0;
    protected static ObjectMapper mapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected CaseService caseService;

    @MockBean
    public CaseRepository caseRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        mapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(this.caseService, this.caseRepository);
    }

}
