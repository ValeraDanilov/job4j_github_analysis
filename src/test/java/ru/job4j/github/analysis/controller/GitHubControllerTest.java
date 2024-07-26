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
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.service.RepositoryService;
import java.time.LocalDateTime;
import java.util.Collections;
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

    @BeforeEach
    public void setUp() {
        this.repository = new Repository();
        this.repository.setName("test-repo");
        this.repository.setUrl("https://github.com/user/test-repo");
        this.repository.setUserName("user");
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
        RepositoryCommits commit = new RepositoryCommits("test-repo", "Initial commit", "user", LocalDateTime.now());
        when(this.repositoryService.findByName("test-repo")).thenReturn(this.repository);
        when(this.repositoryService.findCommitsByRepositoryName(this.repository)).thenReturn(Collections.singletonList(commit));

        this.mockMvc.perform(get("/api/commits/test-repo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test-repo"))
                .andExpect(jsonPath("$[0].message").value("Initial commit"))
                .andExpect(jsonPath("$[0].author").value("user"))
                .andExpect(jsonPath("$[0].date").exists());

        verify(this.repositoryService, times(1)).findByName("test-repo");
        verify(this.repositoryService, times(1)).findCommitsByRepositoryName(this.repository);
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
