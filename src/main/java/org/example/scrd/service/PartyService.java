package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.*;
import org.example.scrd.dto.PartyJoinDto;
import org.example.scrd.dto.PartyPostDetailDto;
import org.example.scrd.dto.PartyPostDto;
import org.example.scrd.dto.request.PartyPostRequest;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.repo.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyPostRepository postRepository;
    private final PartyJoinRepository joinRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final NotificationService notificationService;

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
        PartyPost partyPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("모집글 없음"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저 없음"));

        // 테스트 마치면 주석 풀어야함.
//        if (post.getWriter().getId().equals(user.getId())) {
//            throw new IllegalStateException("작성자는 자신의 모집글에 신청할 수 없습니다.");
//        }

        if (partyPost.isClosed() || partyPost.getCurrentParticipants() >= partyPost.getMaxParticipants()) {
            throw new IllegalStateException("모집이 마감된 파티입니다.");
        }

        if (joinRepository.findByPartyPostIdAndUserId(postId, userId).isPresent()) {
            throw new IllegalStateException("이미 신청함");
        }

        PartyJoin partyJoin = PartyJoin.builder()
                .partyPost(partyPost)
                .user(user)
                .status(PartyJoin.JoinStatus.PENDING)
                .build();

        joinRepository.save(partyJoin);

        // 호스트에게 알림 보내기
        notificationService.notify(
                partyPost.getWriter(),                     // 알림 받는 사람 (호스트)
                user,                                 // 알림 보낸 사람 (신청자)
                Notification.NotificationType.JOIN_REQUEST,
                user.getNickName() + "님이 파티에 참여 신청했습니다.",
                partyPost
        );
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

        // 신청자에게 알림 보내기
        if (newStatus == PartyJoin.JoinStatus.APPROVED) {
            notificationService.notify(
                    join.getUser(),                        // 신청자
                    post.getWriter(),                      // 알림 보낸 사람 (호스트)
                    Notification.NotificationType.APPROVED,
                    "당신의 " + post.getTitle() + " 파티 참여 신청이 승인되었습니다.",
                    post
            );
        } else if (newStatus == PartyJoin.JoinStatus.REJECTED) {
            notificationService.notify(
                    join.getUser(),
                    post.getWriter(),
                    Notification.NotificationType.REJECTED,
                    "당신의 " + post.getTitle() + " 파티 참여 신청이 거절되었습니다.",
                    post
            );
        }
    }

    private PartyJoin.JoinStatus parseStatus(String statusStr) {
        try {
            return PartyJoin.JoinStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 상태입니다: " + statusStr);
        }
    }


//    @Transactional(readOnly = true)
//    public List<PartyJoinDto> getJoinRequests(Long postId, String status) {
//        PartyPost post = postRepository.findById(postId)
//                .orElseThrow(() -> new NotFoundException("모집글 없음"));
//
//        List<PartyJoin> joins;
//        if (status != null) {
//            PartyJoin.JoinStatus joinStatus = PartyJoin.JoinStatus.valueOf(status.toUpperCase());
//            joins = joinRepository.findByPartyPostAndStatus(post, joinStatus);
//        } else {
//            joins = joinRepository.findByPartyPost(post);
//        }
//
//        return joins.stream()
//                .map(join -> PartyJoinDto.from(join))
//                .collect(Collectors.toList());
//    }

    @Transactional
    public void cancelJoin(Long postId, Long userId) {
        // 1) PartyJoin 찾기
        PartyJoin join = joinRepository.findByPartyPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new NotFoundException("신청 내역 없음"));

        // 2) 현재 상태가 APPROVED(승인)였다면 파티 인원 감소
        PartyPost post = join.getPartyPost();
        if (join.getStatus() == PartyJoin.JoinStatus.APPROVED) {
            // 파티 인원 1 감소
            post.decreaseParticipantCount();
        }

        // 3) DB에서 해당 PartyJoin 삭제(물리적 제거)
        //    혹은 'CANCELED' 같은 상태로 업데이트해도 됨
        joinRepository.delete(join);
    }

    @Transactional
    public void deletePartyPost(Long postId, Long userId) {
        PartyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("파티 글 없음"));

        // 1) 작성자인지 권한 확인
        if (!post.getWriter().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        notificationRepository.deleteByRelatedPostId(postId);

        // 2) 파티 글 삭제 (DB에서 물리 삭제)
        postRepository.delete(post);
    }

    public List<PartyPostDto> getPartyPostsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"));
        return postRepository.findAll(pageable)
                .map(PartyPostDto::from)  // 또는 PartyPost → PartyJoinDto 변환
                .getContent();
    }

    public PartyPostDetailDto getPartyPostDetail(Long postId) {
        PartyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("일행 글 없음"));
        return PartyPostDetailDto.from(post);
    }


    @Transactional(readOnly = true)
    public List<PartyJoinDto> getJoinRequestsByWriter(Long writerId) {
        List<PartyJoin> joins = joinRepository.findAllByWriterId(writerId);
        return joins.stream()
                .map(PartyJoinDto::from)
                .collect(Collectors.toList());
    }


}
