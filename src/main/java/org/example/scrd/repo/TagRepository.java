package org.example.scrd.repo;

import org.example.scrd.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    // 필요한 커스텀 메서드가 있다면 여기에 추가
}
