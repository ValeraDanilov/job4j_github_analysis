package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepositoryRepository;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Repository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RepositoryService {

    private final RepositoryRepository repository;
    private final CommitRepository commitRepository;
    private final GitHubRemote gitHubRemote;

    public List<Repository> findAll() {
        return this.repository.findAll();
    }

    public Repository findByName(String name) {
        return this.repository.findByName(name);
    }

    public List<RepositoryCommits> findCommitsByRepositoryName(Repository repository) {
        List<RepositoryCommits> repositoryCommits = new ArrayList<>();
        List<Commit> commits = this.commitRepository.findCommitsByRepositoryName(repository.getName());
        if (commits != null) {
            for (Commit commit : commits) {
                repositoryCommits.add(
                        new RepositoryCommits(
                                repository.getName(),
                                commit.getMessage(),
                                commit.getAuthor(),
                                commit.getDate()
                        )
                );
            }
        }
        return repositoryCommits;
    }

    @Async
    public void create(Commit commit) {
        this.commitRepository.save(commit);
    }

    @Async
    public void create(String userName) {
        List<Repository> repositories = this.gitHubRemote.fetchRepositories(userName);
        for (Repository rep : repositories) {
            rep.setUserName(userName);
            this.repository.save(rep);
        }
    }
}
