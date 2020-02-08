package com.stevenpg.sonarqubeportfoliofree.controller;

import com.stevenpg.sonarqubeportfoliofree.data.StateSingleton;
import com.stevenpg.sonarqubeportfoliofree.loader.ProjectPage;
import com.stevenpg.sonarqubeportfoliofree.models.response.measures.MeasuresResponse;
import com.stevenpg.sonarqubeportfoliofree.models.response.projectsearch.ProjectSearchResponse;
import com.stevenpg.sonarqubeportfoliofree.service.SonarQubeDataProxyService;
import com.stevenpg.sonarqubeportfoliofree.service.SonarQubeDataProxyServicev2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.stevenpg.sonarqubeportfoliofree.data.StateSingleton.getInstance;

@Slf4j
@RestController
public class SonarQubeDataProxyControllerv2 {

    private SonarQubeDataProxyService dataService;
    private SonarQubeDataProxyServicev2 proxyServicev2;

    @Autowired
    public SonarQubeDataProxyControllerv2(SonarQubeDataProxyService dataService, SonarQubeDataProxyServicev2 proxyServicev2) {
        this.dataService = dataService;
        this.proxyServicev2 = proxyServicev2;
    }

    /**
     * This method just returns the JSON output from the service call
     * that makes a search for all projects configured in the configuration file.
     * @return passthru of projects/search API call to SonarQube
     */
    @CrossOrigin({ "*" })
    @GetMapping("/api/v2/searchProjects")
    public @ResponseBody
    Mono<ProjectSearchResponse> apiProxyGet() {
        return dataService.getRequestedProjects();
    }

    /**
     * This method just returns the JSON output from the service call
     * that makes a search for all projects configured in the configuration file.
     * @return passthru of projects/search/{projectKey} API call to SonarQube
     */
    @CrossOrigin({ "*" })
    @GetMapping("/api/v2/searchProjects/{projectkey}")
    public @ResponseBody Mono<ProjectSearchResponse> getProject(@PathVariable("projectkey") String projectKey) {
        return dataService.getSpecificProject(projectKey);
    }

    /**
     * @return group names in array of strings for UI to render and make subsequent calls
     */
    @CrossOrigin({ "*" })
    @GetMapping("/api/v2/projectGroups")
    public @ResponseBody Mono<List<String>> getProjectGroups() {
        List<ProjectPage> configuredPages = StateSingleton.getInstance().getProjectPages();
        return Mono.just(configuredPages.stream().map(projectPage -> {
            return projectPage.getPagename();
        }).collect(Collectors.toList()));
    }

    /**
     * Return measures for project supplied
     */
    @CrossOrigin({ "*" })
    @GetMapping("/api/v2/projectGroups/{projectGroup}/measures")
    public @ResponseBody
    Flux<MeasuresResponse> getMeasuresFromGroup(@PathVariable("projectGroup") String projectGroup) {
        return proxyServicev2.getGroupMeasures(projectGroup);
    }

    /**
     * Return measures for project supplied
     */
    @CrossOrigin({ "*" })
    @GetMapping("/api/v2/projectKeys/{projectKey}/measures")
    public @ResponseBody Mono<MeasuresResponse> getMeasuresFromProject(@PathVariable("projectKey") String projectKey) {
        return proxyServicev2.getProjectMeasures(projectKey);
    }

    /**
     * Get project keys as array for UI to render and make subsequent calls
     * @param projectGroupName name of group to pull
     * @return array of keys from specified project group parameter
     */
    @CrossOrigin({ "*" })
    @GetMapping("/api/v2/projectGroups/{projectGroupName}/projectKeys")
    public @ResponseBody Mono<List<String>> getProjectKeysFromProjectGroup(@PathVariable("projectGroupName") String projectGroupName) {
        return Mono.just(StateSingleton.getInstance().getProjectPages().stream()
                .filter(projectPage -> projectPage.getPagename().equals(projectGroupName))
                .map(ProjectPage::getProjectKeys)
                .flatMap(projectPage -> projectPage.stream())
                .distinct()
                .collect(Collectors.toList()));

    }

    /**
     * @return status code from hitting sonar's /about page
     */
    @CrossOrigin({ "*" })
    @GetMapping("/api/v2/sonar-connection")
    public Mono<HttpStatus> checkSonarConnection() {
        return dataService.getSonarQubeStatus();
    }

    /**
     * @return configured sonar host url within config file
     */
    @CrossOrigin({ "*" })
    @GetMapping("/api/v2/sonar-host")
    public ResponseEntity<String> getSonarHost() {
        return new ResponseEntity<>(getInstance().getSonarhosturl(), HttpStatus.OK);
    }

}
