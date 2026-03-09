package com.gnesis.forum_hub.repository;

import com.gnesis.forum_hub.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    Optional<Curso> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
