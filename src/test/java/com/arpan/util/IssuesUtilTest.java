package com.arpan.util;

import com.arpan.model.NumberOfIssuesModel;
import com.google.common.collect.ImmutableList;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class IssuesUtilTest {

    private static final String OPEN = "open";
    private static final String INVALID_URL = "";
    private static final String ISSUES = "/issues";

    private static List<RepositoryIssue> getRepositoryIssues() {
        RepositoryIssue issueToday = new RepositoryIssue();
        issueToday.setCreatedAt(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));
        issueToday.setState(OPEN);

        RepositoryIssue issueMoreThan24HoursAgo = new RepositoryIssue();
        issueMoreThan24HoursAgo.setCreatedAt(Date.from(Instant.now().minus(28, ChronoUnit.HOURS)));
        issueMoreThan24HoursAgo.setState(OPEN);

        return ImmutableList.of(issueToday, issueMoreThan24HoursAgo);
    }

    private static Optional<NumberOfIssuesModel> getExpectedModel() {
        return Optional.of(NumberOfIssuesModel.builder()
                .issuesInLastOneDay(1L)
                .issuesInLastSevenDays(1L)
                .issuesSevenDaysAgo(0L)
                .totalNumberOfOpenIssues(2L)
                .build());
    }

    @Test
    public void createGitApiUrlEmptyGitUrl() {
        assertThat(IssuesUtil.createGitApiUrl(INVALID_URL, ISSUES)).isEmpty();
    }

    @Test
    @Parameters({"https://github.com/a/b, /repos/a/b/issues",
            "http://github.com/a/b, /repos/a/b/issues",
            "www.github.com/a/b, /repos/a/b/issues"})
    public void createGitApiUrlValidGitUrl(String gitUrl, String expectedUrl) {
        assertThat(IssuesUtil.createGitApiUrl(gitUrl, ISSUES)).isEqualTo(expectedUrl);
    }

    @Test
    public void getNumberOfIssuesModel() {
        List<RepositoryIssue> repositoryIssues = getRepositoryIssues();
        assertThat(IssuesUtil.getNumberOfIssuesModel(repositoryIssues))
                .isEqualTo(getExpectedModel());
    }
}