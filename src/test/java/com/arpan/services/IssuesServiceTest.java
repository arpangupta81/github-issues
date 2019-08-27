package com.arpan.services;

import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class IssuesServiceTest {

    private static final String OPEN = "open";
    private static final String EMPTY_STRING = "";
    private static final String DUMMY_KEY = "ZHVtbXk=";
    private static final String VALID_URL = "www.github.com/a/b";
    @Mock
    private GitHubClient gitHubClient;
    private IssuesService issuesService = new IssuesService(DUMMY_KEY);

    @Test
    public void testGetIssuesWhenUrlIsEmpty() {
        assertThat(issuesService.getIssuesModel(EMPTY_STRING)).isEmpty();
    }

    @Test
    public void testGetIssuesWhenApiGivesNoResponse() {
        assertThat(issuesService.getIssuesModel(VALID_URL))
                .isEmpty();
    }

    @Test
    public void testGetIssuesWhenUrlIsValid() {
        List<RepositoryIssue> issues = new ArrayList<>();

        RepositoryIssue issueToday = new RepositoryIssue();
        issueToday.setCreatedAt(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));
        issueToday.setState(OPEN);

        issues.add(issueToday);
        GitHubResponse gitHubResponse = new GitHubResponse(Mockito.mock(HttpURLConnection.class), issues);
        assertThat(issuesService.getIssuesModel(VALID_URL)).isNotEqualTo(gitHubResponse);

    }
}