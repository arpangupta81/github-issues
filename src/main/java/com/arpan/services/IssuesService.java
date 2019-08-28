package com.arpan.services;

import com.arpan.model.NumberOfIssuesModel;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.IResourceProvider;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.arpan.util.IssuesUtil.createGitApiUrl;
import static com.arpan.util.IssuesUtil.getNumberOfIssuesModel;
import static java.util.Base64.getDecoder;

@Slf4j
@Service
public class IssuesService {
    private static final String ISSUES = "/issues";
    private final String key;
    private final GitHubClient gitHubClient;
    private final Executor executor;

    @Autowired
    public IssuesService(@Value("${key}") String key) {
        this.executor = Executors.newFixedThreadPool(50);
        this.gitHubClient = new GitHubClient();
        this.key = key;
    }

    private List<RepositoryIssue> getAll(String gitApiUrl) {
        List<CompletableFuture<List<RepositoryIssue>>> futures = new ArrayList<>();
        List<RepositoryIssue> repositoryIssues = new ArrayList<>();
        List<GitHubRequest> gitHubRequests = IntStream.rangeClosed(0, 40)
                .boxed()
                .parallel()
                .map(a -> getUri(a, gitApiUrl))
                .collect(Collectors.toList());
        for (GitHubRequest req : gitHubRequests) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    return getIssues(req);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Collections.emptyList();
            }, executor));
        }

        for (CompletableFuture<List<RepositoryIssue>> futureRes : futures) {
            try {
                repositoryIssues.addAll(futureRes.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return repositoryIssues;
    }

    private List<RepositoryIssue> getIssues(GitHubRequest a) throws IOException {
        GitHubResponse gitHubResponse = gitHubClient.get(a);
        Object body = gitHubResponse.getBody();
        if (body != null)
            if (body instanceof Collection)
                return (List<RepositoryIssue>) body;
            else if (body instanceof IResourceProvider)
                return ((IResourceProvider<RepositoryIssue>) body).getResources();
            else {
                RepositoryIssue resp = (RepositoryIssue) body;
                return Collections.singletonList(resp);
            }
        return Collections.emptyList();
    }

    private GitHubRequest getUri(int a, String gitApiUrl) {
        GitHubRequest gitHubRequest = new GitHubRequest();
        String uri = String.format("%s?per_page=100&page=%s", gitApiUrl, a);
        gitHubRequest.setUri(uri);
        gitHubRequest.setType(new TypeToken<List<RepositoryIssue>>() {
        }.getType());
        return gitHubRequest;
    }

    /**
     * This method gives {@link Optional<NumberOfIssuesModel>} from a given git Url.
     *
     * @param gitRepoUrl Git Repository Url.
     * @return Optional of Issues Model.
     */
    public Optional<NumberOfIssuesModel> getIssuesModel(String gitRepoUrl) {
        gitHubClient.setOAuth2Token(new String(getDecoder().decode(key)));

        String gitApiUrl = createGitApiUrl(gitRepoUrl, ISSUES);
        if (Strings.isNullOrEmpty(gitApiUrl)) {
            log.error("Received Invalid URL: {}", gitRepoUrl);
            return Optional.empty();
        }
        List<RepositoryIssue> repositoryIssues = getAll(gitApiUrl);
        return getNumberOfIssuesModel(repositoryIssues);
    }
}