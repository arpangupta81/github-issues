package com.arpan.services;

import com.arpan.model.NumberOfIssuesModel;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.NoSuchPageException;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.arpan.util.IssuesUtil.createGitApiUrl;

@Slf4j
@Service
public class IssuesService {
    private static final String OPEN = "open";
    private static final int TWENTY_FOUR = 24;
    private static final int SEVEN = 7;
    private static final String ISSUES = "/issues";
    private static final Instant INSTANT = Instant.now();
    private final GitHubClient gitHubClient;

    @Autowired
    public IssuesService() {
        this.gitHubClient = new GitHubClient();
    }

    private PageIterator<RepositoryIssue> pageIssues(String uri) {
        PagedRequest<RepositoryIssue> request = new PagedRequest<>(1, 100);
        request.setUri(uri);
        request.setType(new TypeToken<List<RepositoryIssue>>() {
        }.getType());
        return new PageIterator<>(request, gitHubClient);
    }

    private <V> List<V> getAll(PageIterator<V> iterator) throws IOException {
        List<V> elements = new ArrayList<V>();
        try {
            while (iterator.hasNext())
                elements.addAll(iterator.next());
        } catch (NoSuchPageException pageException) {
            throw pageException.getCause();
        }
        return elements;
    }

    public Optional<NumberOfIssuesModel> getIssuesModel(String gitRepoUrl) throws IOException {
        String gitApiUrl = createGitApiUrl(gitRepoUrl, ISSUES);
        if (Strings.isNullOrEmpty(gitApiUrl)) {
            log.error("Received URL is invalid git url.: {}", gitRepoUrl);
            return Optional.empty();
        }

        List<RepositoryIssue> repositoryIssues = getAll(pageIssues(gitApiUrl));

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

    private long getIssuesBetweenStartAndEndTime(List<RepositoryIssue> issuesModel,
                                                 Instant startTime, Instant endTime) {
        return issuesModel.stream()
                .filter(issue -> OPEN.equalsIgnoreCase(issue.getState())
                        && Strings.isNullOrEmpty(issue.getPullRequest().getUrl()))
                .filter(issue -> null != issue.getCreatedAt())
                .filter(issue -> issue.getCreatedAt().toInstant().isAfter(startTime)
                        && issue.getCreatedAt().toInstant().isBefore(endTime))
                .count();
    }
}