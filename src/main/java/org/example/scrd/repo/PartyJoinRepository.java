package org.example.scrd.repo;

import org.example.scrd.domain.PartyJoin;
import org.example.scrd.domain.PartyPost;
import org.example.scrd.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyJoinRepository extends JpaRepository<PartyJoin, Long> {
    List<PartyJoin> findByPartyPostId(Long postId);
    Optional<PartyJoin> findByPartyPostIdAndUserId(Long PartyPostId, Long userId);
    List<PartyJoin> findByPartyPostIdAndStatus(Long PartyPostId, PartyJoin.JoinStatus status);

    List<PartyJoin> findByPartyPost(PartyPost post);
    List<PartyJoin> findByPartyPostAndStatus(PartyPost post, PartyJoin.JoinStatus status);

    Optional<PartyJoin> findByPartyPostAndUser(PartyPost partyPost,User user);
    @Query("SELECT j FROM PartyJoin j JOIN FETCH j.partyPost p WHERE p.writer.id = :writerId")
    List<PartyJoin> findAllByWriterId(@Param("writerId") Long writerId);

}
