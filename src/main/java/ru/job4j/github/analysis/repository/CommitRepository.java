package ru.job4j.github.analysis.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.github.analysis.model.Commit;

import java.util.List;

public interface CommitRepository extends CrudRepository<Commit, Long> {

    List<Commit> findCommitsByRepositoryName(String name);
}
