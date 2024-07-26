package ru.job4j.github.analysis.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.github.analysis.model.Repository;

import java.util.List;

public interface RepositoryRepository extends CrudRepository<Repository, Long> {

    List<Repository> findAll();

    Repository findByName(String name);
}
