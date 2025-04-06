package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.PartyJoin;
import org.example.scrd.domain.PartyPost;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.dto.PartyJoinDto;
import org.example.scrd.dto.request.PartyPostRequest;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.repo.PartyJoinRepository;
import org.example.scrd.repo.PartyPostRepository;
import org.example.scrd.repo.ThemeRepository;
import org.example.scrd.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyPostRepository postRepository;
    private final PartyJoinRepository joinRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public Long createPartyPost(Long writerId, Long themeId, PartyPostRequest request) {
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundException("유저 없음"));
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("테마 없음"));

        PartyPost post = PartyPost.builder()
                .writer(writer)
                .theme(theme)
                .title(request.getTitle())
                .content(request.getContent())
                .maxParticipants(request.getMaxParticipants())
                .deadline(request.getDeadline())
                .isClosed(false)
                .currentParticipants(1) // 글쓴이 포함
                .build();

        return postRepository.save(post).getId();
    }

    @Transactional
    public void joinParty(Long postId, Long userId) {
        PartyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("모집글 없음"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저 없음"));

        if (post.getWriter().getId().equals(user.getId())) {
            throw new IllegalStateException("작성자는 자신의 모집글에 신청할 수 없습니다.");
        }

        if (post.isClosed() || post.getCurrentParticipants() >= post.getMaxParticipants()) {
            throw new IllegalStateException("모집이 마감된 파티입니다.");
        }

        if (joinRepository.findByPartyPostIdAndUserId(postId, userId).isPresent()) {
            throw new IllegalStateException("이미 신청함");
        }

        PartyJoin partyJoin = PartyJoin.builder()
                .partyPost(post)
                .user(user)
                .status(PartyJoin.JoinStatus.PENDING)
                .build();

        joinRepository.save(partyJoin);
    }

    @Transactional
    public void updateJoinStatus(Long joinId, String statusStr) {
        PartyJoin join = joinRepository.findById(joinId)
                .orElseThrow(() -> new NotFoundException("신청 내역을 찾을 수 없습니다."));

        PartyJoin.JoinStatus newStatus = parseStatus(statusStr);
        PartyJoin.JoinStatus currentStatus = join.getStatus();

        PartyPost post = join.getPartyPost();

        // 승인 → 취소(거절) 로 바뀔 때 인원 감소
        if (currentStatus == PartyJoin.JoinStatus.APPROVED && newStatus != PartyJoin.JoinStatus.APPROVED) {
            post.decreaseParticipantCount();
        }

        // 미승인 상태 → 승인 으로 바뀔 때 인원 증가
        if (currentStatus != PartyJoin.JoinStatus.APPROVED && newStatus == PartyJoin.JoinStatus.APPROVED) {
            if (post.isClosed() || post.getCurrentParticipants() >= post.getMaxParticipants()) {
                throw new IllegalStateException("파티 인원이 이미 가득 찼습니다.");
            }
            post.increaseParticipantCount();
        }

        join.setStatus(newStatus);
    }

    private PartyJoin.JoinStatus parseStatus(String statusStr) {
        try {
            return PartyJoin.JoinStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 상태입니다: " + statusStr);
        }
    }


    @Transactional(readOnly = true)
    public List<PartyJoinDto> getJoinRequests(Long postId, String status) {
        PartyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("모집글 없음"));

        List<PartyJoin> joins;
        if (status != null) {
            PartyJoin.JoinStatus joinStatus = PartyJoin.JoinStatus.valueOf(status.toUpperCase());
            joins = joinRepository.findByPartyPostAndStatus(post, joinStatus);
        } else {
            joins = joinRepository.findByPartyPost(post);
        }

        return joins.stream()
                .map(join -> PartyJoinDto.from(join))
                .collect(Collectors.toList());
    }


}
