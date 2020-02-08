package com.stevenpg.sonarqubeportfoliofree.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevenpg.sonarqubeportfoliofree.config.Measures;
import com.stevenpg.sonarqubeportfoliofree.data.StateSingleton;
import com.stevenpg.sonarqubeportfoliofree.loader.ProjectPage;
import com.stevenpg.sonarqubeportfoliofree.models.response.measures.Measure;
import com.stevenpg.sonarqubeportfoliofree.models.response.measures.MeasureBaseComponent;
import com.stevenpg.sonarqubeportfoliofree.models.response.measures.MeasuresResponse;
import com.stevenpg.sonarqubeportfoliofree.models.response.projectsearch.PagingData;
import com.stevenpg.sonarqubeportfoliofree.models.response.projectsearch.ProjectSearchResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
class SonarQubeDataProxyServiceTest {

    public static MockWebServer mockSonarUrl;
    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private SonarQubeDataProxyService service = new SonarQubeDataProxyService();

    @BeforeEach
    void setUp() throws IOException {
        ArrayList<ProjectPage> projectPages = new ArrayList<>();

        ProjectPage samplePage = new ProjectPage();
        samplePage.setPagename("samplePageName");

        ArrayList projectKeys = new ArrayList<String>();
        projectKeys.add("sampleProjectKey");

        samplePage.setProjectKeys(projectKeys);

        projectPages.add(samplePage);

        mockSonarUrl = new MockWebServer();
        mockSonarUrl.start();
        StateSingleton.getInstance().setSonarhosturl(mockSonarUrl.getHostName() + ":" + mockSonarUrl.getPort());
        StateSingleton.getInstance().setBasicAuthHeader("BasicHeader");
        StateSingleton.getInstance().setProjectPages(projectPages);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockSonarUrl.shutdown();
    }

    @Test
    void getRequestedProjects() throws IOException {
        // Build component output
        ProjectSearchResponse response = new ProjectSearchResponse();
        String jsonString = mapper.writeValueAsString(response);

        mockSonarUrl.enqueue(new MockResponse()
                .setBody(jsonString)
                .addHeader("Content-Type", "application/json")
        );

        assertEquals(response, service.getRequestedProjects().block());
    }

    @Test
    void getSonarQubeStatus() {
        mockSonarUrl.enqueue(new MockResponse().setBody(mockSonarUrl.getHostName() + ":" + mockSonarUrl.getPort()));
        assertEquals(HttpStatus.OK, service.getSonarQubeStatus().block());
    }

    @Test
    void getSpecificProject() throws JsonProcessingException {
        // Build component output
        ProjectSearchResponse response = new ProjectSearchResponse();
        String jsonString = mapper.writeValueAsString(response);

        mockSonarUrl.enqueue(new MockResponse()
                .setBody(jsonString)
                .addHeader("Content-Type", "application/json")
        );
        assertEquals(response, service.getSpecificProject("sampleProjectKey").block());
    }

    @Test
    void getMeasuresForProject() throws JsonProcessingException {
        // Build component output
        MeasuresResponse response = new MeasuresResponse();
        MeasureBaseComponent baseComponent = new MeasureBaseComponent();
        baseComponent.setMeasures(List.of(new Measure()));
        response.setBaseComponent(baseComponent);
        response.setPage(new PagingData());

        String jsonString = mapper.writeValueAsString(response);

        mockSonarUrl.enqueue(new MockResponse()
                .setBody(jsonString)
                .addHeader("Content-Type", "application/json")
        );
        mockSonarUrl.enqueue(new MockResponse()
                .setBody(jsonString)
                .addHeader("Content-Type", "application/json")
        );

        // Add another measure
        response.getBaseComponent().setMeasures(List.of(new Measure(), new Measure()));

        assertEquals(response, service.getMeasuresForProject("sampleProjectKey").block());
    }
}