package ru.job4j.github.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryCommits {
    private String name;
    private String message;
    private String author;
    private LocalDateTime date;
}
