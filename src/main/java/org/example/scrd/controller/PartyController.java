package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.controller.response.ApiResponse;
import org.example.scrd.domain.User;
import org.example.scrd.dto.PartyJoinDto;
import org.example.scrd.dto.PartyPostDetailDto;
import org.example.scrd.dto.PartyPostDto;
import org.example.scrd.dto.request.PartyJoinRequest;
import org.example.scrd.dto.request.PartyPostRequest;
import org.example.scrd.service.PartyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/scrd/api/party")
public class PartyController {

    private final PartyService partyService;

    //TODO : 일행 GET mapping 필요함
    // 전체 일행 모집 글을 페이징 기반으로 조회
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<List<PartyPostDto>>> getPartyPostsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
            @RequestParam(required = false) Boolean isClosed) {

        List<PartyPostDto> posts = partyService.getPartyPostsPaged(page, size, deadline, isClosed);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }


    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PartyPostDetailDto>> getPartyPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user){
        PartyPostDetailDto dto = partyService.getPartyPostDetail(postId, user);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }


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

    // 파티 참여 신청을 승인 / 거절 처리하는 API를 위한 전체 구성입니다. 파티장 입장에서 수락 혹은 거절
    @PostMapping("/join/{joinId}/status")
    public ResponseEntity<ApiResponse<Object>> updateJoinStatus(
            @PathVariable Long joinId,
            @RequestBody PartyJoinRequest request) {
        partyService.updateJoinStatus(joinId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success());
    }


    // 파티장이 신청자의 일행 신청 글들을 확인하는 API
    @GetMapping("/join/notification")
    public ResponseEntity<ApiResponse<List<PartyJoinDto>>> getJoinRequestsByWriter(
            @AuthenticationPrincipal User user) {
        List<PartyJoinDto> joins = partyService.getJoinRequestsByWriter(user.getId());
        return ResponseEntity.ok(ApiResponse.success(joins));
    }

    // 알림 창에서 사용자가 본인이 신청했던 일행의 수락 혹은 거절 상태를 알 수 있는 API
    @GetMapping("/join/status")
    public ResponseEntity<ApiResponse<List<PartyJoinDto>>> getMyJoinStatus(
            @AuthenticationPrincipal User user) {
        List<PartyJoinDto> joins = partyService.getMyResolvedJoins(user.getId());
        return ResponseEntity.ok(ApiResponse.success(joins));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Object>> deletePartyPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        partyService.deletePartyPost(postId, user); // ✅ 그냥 user 전체 넘기는 것도 깔끔
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/{postId}/join")
    public ResponseEntity<ApiResponse<Object>> cancelJoin(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        partyService.cancelJoin(postId, user.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }

}


