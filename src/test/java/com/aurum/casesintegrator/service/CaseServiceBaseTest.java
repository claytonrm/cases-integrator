package com.aurum.casesintegrator.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.aurum.casesintegrator.repository.CaseRepository;

@SpringBootTest
@DisplayName("[CaseService] - Unit Tests for Cases Services")
public class CaseServiceBaseTest {

    @Autowired
    protected CaseService caseService;

    @MockBean
    protected CaseRepository caseRepository;

    @AfterEach
    public void tearDown() {
        Mockito.reset(this.caseRepository);
    }

}
