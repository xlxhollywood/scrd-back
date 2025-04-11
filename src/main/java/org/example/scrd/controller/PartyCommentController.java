package org.example.scrd.controller;

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
public class PartyCommentController {

    private final PartyCommentService commentService;

    @PostMapping
    public ResponseEntity<Void> addComment(@RequestBody PartyCommentRequest request,
                                           @AuthenticationPrincipal User user) {
        commentService.addComment(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<PartyCommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }

}
