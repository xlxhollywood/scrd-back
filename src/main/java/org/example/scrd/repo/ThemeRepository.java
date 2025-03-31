package org.example.scrd.repo;

import org.example.scrd.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    List<Theme> findByTitleContainingOrBrandContaining(String title, String brand);

}
