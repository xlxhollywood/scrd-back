package org.example.scrd.repo;

import org.example.scrd.domain.SavedTheme;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedThemeRepository extends JpaRepository<SavedTheme, Long> {
    void deleteByUserAndTheme(User user, Theme theme);
    List<SavedTheme> findByUser(User user);
}
