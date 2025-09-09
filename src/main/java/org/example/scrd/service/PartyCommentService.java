package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.PartyComment;
import org.example.scrd.domain.PartyPost;
import org.example.scrd.domain.Role;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.PartyCommentRequest;
import org.example.scrd.dto.response.PartyCommentResponse;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.exception.UnauthorizedAccessException;
import org.example.scrd.repo.PartyCommentRepository;
import org.example.scrd.repo.PartyPostRepository;
import org.example.scrd.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyCommentService {

    private final PartyCommentRepository commentRepository;
    private final PartyPostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void addComment(Long userId, PartyCommentRequest request) {
        PartyPost post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글 없음"));
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // 대댓글 확인
        PartyComment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글 없음"));
        }

        PartyComment comment = PartyComment.builder()
                .post(post)
                .writer(writer)
                .content(request.getContent())
                .parent(parent)  // null이어도 상관없음
                .build();

        // DB 저장
        PartyComment savedComment = commentRepository.save(comment);

        // 알림 발송
        if (savedComment.getParent() == null) {
            sendCommentNotification(savedComment);
        } else {
            sendReplyNotification(savedComment);
        }
    }

    public List<PartyCommentResponse> getCommentsByPost(Long postId) {
        List<PartyComment> allComments = commentRepository.findByPostId(postId);

        return allComments.stream()
                .filter(comment -> comment.getParent() == null) // 루트 댓글만
                .map(PartyCommentResponse::fromEntity)          // 자식은 DTO 내부에서 재귀적으로 처리
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        PartyComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("해당 댓글이 존재하지 않습니다."));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = comment.getWriter().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException();
        }

        // 관련 알림 삭제
        notificationService.deleteNotificationsByComment(commentId);

        // 하위 대댓글도 같이 삭제됨 (Cascade 설정 덕분에)
        commentRepository.delete(comment);
    }



}

