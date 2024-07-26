package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.CommitRepository;
import ru.job4j.github.analysis.repository.RepositoryRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {

    @Mock
    private RepositoryRepository repositoryRepository;

    @Mock
    private CommitRepository commitRepository;

    @Mock
    private GitHubRemote gitHubRemote;

    @InjectMocks
    private RepositoryService repositoryService;

    private Repository repository;
    private Commit commit;

    @BeforeEach
    public void setUp() {
        this.repository = new Repository();
        this.repository.setName("test-repo");
        this.repository.setUrl("https://github.com/user/test-repo");

        this.commit = new Commit();
        this.commit.setMessage("Initial commit");
        this.commit.setAuthor("user");
        this.commit.setDate(LocalDateTime.now());
        this.commit.setRepository(this.repository);
    }

    @Test
    void testFindAll() {
        when(this.repositoryRepository.findAll()).thenReturn(Collections.singletonList(this.repository));
        List<Repository> repositories = this.repositoryService.findAll();
        assertEquals(1, repositories.size());
        assertEquals("test-repo", repositories.get(0).getName());
        verify(this.repositoryRepository, times(1)).findAll();
    }

    @Test
    void testFindByName() {
        when(this.repositoryRepository.findByName("test-repo")).thenReturn(this.repository);
        Repository foundRepository = this.repositoryService.findByName("test-repo");
        assertEquals("test-repo", foundRepository.getName());
        verify(this.repositoryRepository, times(1)).findByName("test-repo");
    }

    @Test
    void testFindCommitsByRepositoryName() {
        when(this.commitRepository.findCommitsByRepositoryName("test-repo")).thenReturn(Collections.singletonList(this.commit));
        List<RepositoryCommits> commits = this.repositoryService.findCommitsByRepositoryName(this.repository);
        assertEquals(1, commits.size());
        assertEquals("Initial commit", commits.get(0).getMessage());
        verify(this.commitRepository, times(1)).findCommitsByRepositoryName("test-repo");
    }

    @Test
    void testCreateCommit() {
        this.repositoryService.create(this.commit);
        verify(this.commitRepository, times(1)).save(this.commit);
    }

    @Test
    void testCreateRepositoriesForUser() {
        when(this.gitHubRemote.fetchRepositories("user")).thenReturn(Collections.singletonList(this.repository));
        this.repositoryService.create("user");
        verify(this.gitHubRemote, times(1)).fetchRepositories("user");
        verify(this.repositoryRepository, times(1)).save(this.repository);
    }
}
