package org.example.scrd.repo;

import org.example.scrd.domain.PartyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PartyPostRepositoryCustom {
    Page<PartyPost> findByConditions(LocalDate deadline, Boolean isClosed, Pageable pageable);
}
