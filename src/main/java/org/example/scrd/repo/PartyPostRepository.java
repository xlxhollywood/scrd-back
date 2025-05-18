package org.example.scrd.repo;

import org.example.scrd.domain.PartyPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartyPostRepository extends JpaRepository<PartyPost, Long>, PartyPostRepositoryCustom {
    List<PartyPost> findByIsClosedFalse();
}
