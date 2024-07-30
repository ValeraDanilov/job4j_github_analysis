package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class GitHubRemoteTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GitHubRemote gitHubRemote;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchRepositories() {
        String username = "octocat";
        String url = "https://api.github.com/users/" + username + "/repos";
        Repository repository = new Repository();
        repository.setName("Hello-World");
        List<Repository> mockResponse = Collections.singletonList(repository);
        when(this.restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        List<Repository> repositories = this.gitHubRemote.fetchRepositories(username);
        assertEquals(1, repositories.size());
        assertEquals("Hello-World", repositories.get(0).getName());
    }

    @Test
    void testFetchCommits() {
        String owner = "octocat";
        String repoName = "Hello-World";
        String sha = "abcdef123456";
        String url = String.format("https://api.github.com/repos/%s/%s/commits?sha=%s", owner, repoName, sha);
        Commit commit = new Commit();
        commit.setMessage("Initial commit");
        List<Commit> mockResponse = Collections.singletonList(commit);
        when(this.restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        List<Commit> commits = this.gitHubRemote.fetchCommits(owner, repoName, sha);
        assertEquals(1, commits.size());
        assertEquals("Initial commit", commits.get(0).getMessage());
    }

    @Test
    void testFetchRepositoriesWhenEmpty() {
        String username = "octocat";
        String url = "https://api.github.com/users/" + username + "/repos";
        List<Repository> mockResponse = Collections.emptyList();

        when(this.restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        List<Repository> repositories = this.gitHubRemote.fetchRepositories(username);
        assertEquals(0, repositories.size());
    }

    @Test
    void testFetchCommitsWhenEmpty() {
        String owner = "octocat";
        String repoName = "Hello-World";
        String sha = "abcdef123456";
        String url = String.format("https://api.github.com/repos/%s/%s/commits?sha=%s", owner, repoName, sha);
        List<Commit> mockResponse = Collections.emptyList();
        when(this.restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        List<Commit> commits = this.gitHubRemote.fetchCommits(owner, repoName, sha);
        assertEquals(0, commits.size());
    }

    @Test
    void testFetchRepositoriesWhenError() {
        String username = "octocat";
        String url = "https://api.github.com/users/" + username + "/repos";
        when(this.restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        List<Repository> repositories = this.gitHubRemote.fetchRepositories(username);
        assertNull(repositories);
    }

    @Test
    void testFetchCommitsWhenError() {
        String owner = "octocat";
        String repoName = "Hello-World";
        String sha = "abcdef123456";
        String url = String.format("https://api.github.com/repos/%s/%s/commits?sha=%s", owner, repoName, sha);
        when(this.restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        List<Commit> commits = this.gitHubRemote.fetchCommits(owner, repoName, sha);
        assertNull(commits);
    }
}
