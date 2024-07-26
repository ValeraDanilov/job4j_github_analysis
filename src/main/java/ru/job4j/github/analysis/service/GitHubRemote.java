package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.util.List;

@Service
@AllArgsConstructor
public class GitHubRemote {

    private final RestTemplate restTemplate;

    public List<Repository> fetchRepositories(String username) {
        String url = "https://api.github.com/users/" + username + "/repos";
        ResponseEntity<List<Repository>> response = this.restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    public List<Commit> fetchCommits(String owner, String repoName) {
        String url = String.format("https://api.github.com/repos/%s/%s/commits", owner, repoName);
        ResponseEntity<List<Commit>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }
}
