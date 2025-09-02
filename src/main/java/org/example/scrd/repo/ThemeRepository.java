package org.example.scrd.repo;

import org.example.scrd.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long>, ThemeRepositoryCustom {
}
