package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.controller.response.ApiResponse;
import org.example.scrd.domain.User;
import org.example.scrd.dto.PartyJoinDto;
import org.example.scrd.dto.request.PartyJoinRequest;
import org.example.scrd.dto.request.PartyPostRequest;
import org.example.scrd.service.PartyService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/scrd/api/party")
public class PartyController {

    private final PartyService partyService;

    //TODO : 일행 post GET mapping 필요함




    @PostMapping("/{themeId}")
    public ResponseEntity<ApiResponse<Long>> createPost(
            @PathVariable Long themeId,
            @RequestBody PartyPostRequest request,
            @AuthenticationPrincipal User user) {
        Long id = partyService.createPartyPost(user.getId(), themeId, request);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    // 파티 조인 컨트롤러, 참가자 입장에서 파티 신청 보낸 거
    @PostMapping("/{PartyPostId}/join")
    public ResponseEntity<ApiResponse<Object>> joinPost(
            @PathVariable Long PartyPostId,
            @AuthenticationPrincipal User user) {
        partyService.joinParty(PartyPostId, user.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 파티 참여 신청을 승인 / 거절 처리하는 API를 위한 전체 구성입니다. 파티장입장에서 수락 혹은 거절
    @PostMapping("/join/{joinId}/status")
    public ResponseEntity<ApiResponse<Object>> updateJoinStatus(
            @PathVariable Long joinId,
            @RequestBody PartyJoinRequest request) {
        partyService.updateJoinStatus(joinId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 알림 창에서 파티 참여 글을 확인하는 ** API 일행 신청자들 목록 확인 (PENDING만 필터링)
    @GetMapping("/{postId}/joins")
    public ResponseEntity<ApiResponse<List<PartyJoinDto>>> getJoinRequests(
            @PathVariable Long postId,
            @RequestParam(required = false) String status) {
        System.out.println("DELETE method");
        List<PartyJoinDto> joins = partyService.getJoinRequests(postId, status);
        return ResponseEntity.ok(ApiResponse.success(joins));
    }


    // 일행 모집 글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Object>> deletePartyPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        partyService.deletePartyPost(postId, user.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    //TODO : 신청한 요청을 취소
    @DeleteMapping("/{postId}/join")
    public ResponseEntity<ApiResponse<Object>> cancelJoin(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        partyService.cancelJoin(postId, user.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }



}


