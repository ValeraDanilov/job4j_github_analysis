package ru.job4j.github.analysis.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.util.List;

public interface CommitRepository extends CrudRepository<Commit, Long> {

    @Query("""
            SELECT c FROM Commit c JOIN c.repository r WHERE r.name = :name
            """)
    List<Commit> findCommitsByRepositoryName(String name);
    Commit findTopByRepositoryOrderByDateDesc(Repository repository);
}
