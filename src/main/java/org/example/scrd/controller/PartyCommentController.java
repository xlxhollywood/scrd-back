package org.example.scrd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.PartyCommentRequest;
import org.example.scrd.dto.response.PartyCommentResponse;
import org.example.scrd.service.PartyCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scrd/api/party/comment")
@RequiredArgsConstructor
@Tag(name = "PartyComment", description = "일행 모집 댓글 관리")
@SecurityRequirement(name = "Bearer Authentication")
public class PartyCommentController {

    private final PartyCommentService commentService;

    @Operation(summary = "댓글 작성", description = "일행 모집 글에 댓글 또는 대댓글을 작성합니다")
    @PostMapping
    public ResponseEntity<Void> addComment(@RequestBody PartyCommentRequest request,
                                           @AuthenticationPrincipal User user) {
        commentService.addComment(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 목록 조회", description = "특정 일행 모집 글의 모든 댓글을 조회합니다")
    @GetMapping("/{postId}")
    public ResponseEntity<List<PartyCommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @Operation(summary = "댓글 삭제", description = "내가 작성한 댓글을 삭제합니다")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }

}
