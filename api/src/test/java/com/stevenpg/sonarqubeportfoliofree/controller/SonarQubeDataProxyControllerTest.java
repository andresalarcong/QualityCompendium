package com.stevenpg.sonarqubeportfoliofree.controller;

import com.stevenpg.sonarqubeportfoliofree.models.response.projectsearch.PagingData;
import com.stevenpg.sonarqubeportfoliofree.models.response.projectsearch.ProjectSearchResponse;
import com.stevenpg.sonarqubeportfoliofree.models.response.projectsearch.SonarComponent;
import com.stevenpg.sonarqubeportfoliofree.service.SonarQubeDataProxyService;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@RunWith(MockitoJUnitRunner.class)
class SonarQubeDataProxyControllerTest {

    @Mock
    private SonarQubeDataProxyService dataService;

    @InjectMocks
    private SonarQubeDataProxyController proxyController = new SonarQubeDataProxyController(dataService);

    // Data
    private ProjectSearchResponse projectSearchResponse;
    private ArrayList<SonarComponent> components;
    private PagingData pagingData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        projectSearchResponse = new ProjectSearchResponse();

        pagingData = new PagingData();
        components = new ArrayList<>();
        components.add(new SonarComponent());
        projectSearchResponse.setComponentList(components);

        projectSearchResponse.setPage(pagingData);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void apiProxyGet() {

    }

    @Test
    void getProject() {
    }

    @Test
    void getMeasures() {
    }

    @Test
    void getPages() {
    }

    @Test
    void checkSonarConnection() {
    }

    @Test
    void getSonarHost() {
    }
}