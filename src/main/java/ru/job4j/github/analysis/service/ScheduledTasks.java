package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.util.List;

@AllArgsConstructor
@Service
public class ScheduledTasks {

    private final RepositoryService repositoryService;
    private final GitHubRemote gitHubRemote;

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchCommits() {
        var repositories = this.repositoryService.findAll();
        for (Repository repository : repositories) {
            Commit latestCommit = this.repositoryService.findLatestCommit(repository);
            String lastCommitSha = latestCommit != null ? latestCommit.getSha() : null;
            List<Commit> commits = this.gitHubRemote.fetchCommits(repository.getUserName(), repository.getName(), lastCommitSha);
            if (!commits.isEmpty()) {
                for (Commit commit : commits) {
                    commit.setRepository(repository);
                    this.repositoryService.create(commit);
                }
            }
        }
    }
}
