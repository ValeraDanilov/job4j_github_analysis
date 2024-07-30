package ru.job4j.github.analysis.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.service.RepositoryService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class GitHubController {

    private RepositoryService repositoryService;

    @GetMapping("/repositories")
    public List<Repository> getAllRepositories() {
        return this.repositoryService.findAll();
    }

    @GetMapping("/commits/{name}")
    public List<Commit> getCommits(@PathVariable(value = "name") String name) {
        return this.repositoryService.findCommitsByRepositoryName(name);
    }

    @PostMapping("/gitHub/{userName}")
    public ResponseEntity<Void> create(@PathVariable(value = "userName") String userName) {
        this.repositoryService.create(userName);
        return ResponseEntity.noContent().build();
    }
}
