package com.arpan.util;

import com.arpan.model.NumberOfIssuesModel;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.eclipse.egit.github.core.RepositoryIssue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class IssuesUtil {

    private static final int SEVEN = 7;
    private static final int TWENTY_FOUR = 24;
    private static final String OPEN = "open";
    private static final String EMPTY_STRING = "";
    private static final String FORMAT = "/repos/%s%s";
    private static final Instant INSTANT = Instant.now();
    private static final Splitter SPLITTER_WWW = Splitter.on("www.github.com/").omitEmptyStrings().trimResults();
    private static final Splitter SPLITTER_HTTP = Splitter.on("http://github.com/").omitEmptyStrings().trimResults();
    private static final Splitter SPLITTER_HTTPS = Splitter.on("https://github.com/").omitEmptyStrings().trimResults();

    private IssuesUtil() {
    }

    /**
     * Create Api Url from the github repository url.
     *
     * @param gitUrl Github Url.
     * @param type   Type of request whether for issues or pr's.
     * @return {@link String} of api Url.
     */
    public static String createGitApiUrl(String gitUrl, String type) {
        return getSplitOfUrl(SPLITTER_HTTPS, gitUrl, type)
                .orElseGet(() -> tryWithHttp(gitUrl, type));
    }

    private static String tryWithHttp(String gitUrl, String type) {
        return getSplitOfUrl(SPLITTER_HTTP, gitUrl, type)
                .orElseGet(() -> tryWithWww(gitUrl, type));
    }

    private static String tryWithWww(String gitUrl, String type) {
        return getSplitOfUrl(SPLITTER_WWW, gitUrl, type)
                .orElse(EMPTY_STRING);
    }

    private static Optional<String> getSplitOfUrl(Splitter splitter, String gitUrl, String type) {
        return splitter.splitToList(gitUrl).stream().findFirst()
                .filter(url -> !url.equalsIgnoreCase(gitUrl))
                .map(url -> String.format(FORMAT, url, type));
    }

    private static long getIssuesBetweenStartAndEndTime(List<RepositoryIssue> issuesModel,
                                                        Instant startTime, Instant endTime) {
        // Check if the issue is open and is not a pull request
        return issuesModel.stream()
                .filter(issue -> OPEN.equalsIgnoreCase(issue.getState())
                        && (null == issue.getPullRequest() || Strings.isNullOrEmpty(issue.getPullRequest().getUrl())))
                .filter(issue -> null != issue.getCreatedAt())
                .filter(issue -> issue.getCreatedAt().toInstant().isAfter(startTime)
                        && issue.getCreatedAt().toInstant().isBefore(endTime))
                .count();
    }

    /**
     * Gives Optional of {@link NumberOfIssuesModel} model.
     *
     * @param repositoryIssues List of Issues from the Github API.
     * @return Optional of model which stores the stats for open issues.
     */
    public static Optional<NumberOfIssuesModel> getNumberOfIssuesModel(List<RepositoryIssue> repositoryIssues) {
        long numberOfOpenIssues = getIssuesBetweenStartAndEndTime(repositoryIssues, Instant.MIN, Instant.MAX);
        long issuesInLastOneDay = getIssuesBetweenStartAndEndTime(repositoryIssues,
                INSTANT.minus(TWENTY_FOUR, ChronoUnit.HOURS), INSTANT);
        long issuesInLastSevenDays = getIssuesBetweenStartAndEndTime(repositoryIssues,
                INSTANT.minus(SEVEN, ChronoUnit.DAYS), INSTANT.minus(TWENTY_FOUR, ChronoUnit.HOURS));
        long issuesSevenDaysAgo = getIssuesBetweenStartAndEndTime(repositoryIssues,
                Instant.MIN, INSTANT.minus(SEVEN, ChronoUnit.DAYS));

        return Optional.of(NumberOfIssuesModel.builder()
                .totalNumberOfOpenIssues(numberOfOpenIssues)
                .issuesInLastOneDay(issuesInLastOneDay)
                .issuesInLastSevenDays(issuesInLastSevenDays)
                .issuesSevenDaysAgo(issuesSevenDaysAgo)
                .build());
    }
}
