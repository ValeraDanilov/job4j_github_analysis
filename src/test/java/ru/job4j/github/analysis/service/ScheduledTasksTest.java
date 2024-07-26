package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksTest {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private GitHubRemote gitHubRemote;

    @InjectMocks
    private ScheduledTasks scheduledTasks;

    private Repository repository;
    private Commit commit;

    @BeforeEach
    public void setUp() {
        this.repository = new Repository();
        this.repository.setName("test-repo");
        this.repository.setUrl("https://github.com/user/test-repo");
        this.repository.setUserName("user");
        this.commit = new Commit();
        this.commit.setMessage("Initial commit");
        this.commit.setAuthor("user");
        this.commit.setDate(LocalDateTime.now());
        this.commit.setRepository(this.repository);
    }

    @Test
    void testFetchCommits() {
        when(this.repositoryService.findAll()).thenReturn(Collections.singletonList(this.repository));
        when(this.gitHubRemote.fetchCommits(anyString(), anyString())).thenReturn(Collections.singletonList(this.commit));
        this.scheduledTasks.fetchCommits();
        verify(this.repositoryService, times(1)).findAll();
        verify(this.gitHubRemote, times(1)).fetchCommits("user", "test-repo");
        verify(this.repositoryService, times(1)).create(this.commit);
        assertEquals(this.repository, this.commit.getRepository());
    }
}
