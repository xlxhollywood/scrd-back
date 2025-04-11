package org.example.scrd.repo;

import org.example.scrd.domain.PartyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartyCommentRepository extends JpaRepository<PartyComment, Long> {
    List<PartyComment> findByPostIdAndParentIsNullOrderByRegDateAsc(Long postId); // 일반 댓글들
    List<PartyComment> findByParentIdOrderByRegDateAsc(Long parentId); // 특정 댓글의 대댓글

    List<PartyComment> findByPostId(Long postId);

}
