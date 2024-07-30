package ru.job4j.github.analysis.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.service.RepositoryService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(GitHubController.class)
@ExtendWith(MockitoExtension.class)
class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryService repositoryService;

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
        this.commit.setSha("abcd1234");
    }

    @Test
    void testGetAllRepositories() throws Exception {
        when(this.repositoryService.findAll()).thenReturn(Collections.singletonList(this.repository));
        this.mockMvc.perform(get("/api/repositories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test-repo"))
                .andExpect(jsonPath("$[0].url").value("https://github.com/user/test-repo"));
        verify(this.repositoryService, times(1)).findAll();
    }

    @Test
    void testGetCommits() throws Exception {
        List<Commit> commits = Collections.singletonList(this.commit);
        when(this.repositoryService.findCommitsByRepositoryName("test-repo")).thenReturn(commits);

        this.mockMvc.perform(get("/api/commits/test-repo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Initial commit"))
                .andExpect(jsonPath("$[0].author").value("user"))
                .andExpect(jsonPath("$[0].date").exists())
                .andExpect(jsonPath("$[0].sha").value("abcd1234"));

        verify(this.repositoryService, times(1)).findCommitsByRepositoryName("test-repo");
    }

    @Test
    void testCreate() throws Exception {
        doNothing().when(this.repositoryService).create("user");
        this.mockMvc.perform(post("/api/gitHub/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(this.repositoryService, times(1)).create("user");
    }
}
