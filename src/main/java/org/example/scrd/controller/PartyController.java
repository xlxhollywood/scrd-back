package org.example.scrd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Party", description = "일행 모집 관리")
@SecurityRequirement(name = "Bearer Authentication")
public class PartyController {

    private final PartyService partyService;


    @Operation(summary = "일행 모집 글 목록 조회", description = "페이징 기반으로 일행 모집 글을 조회합니다")
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<List<PartyPostDto>>> getPartyPostsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
            @RequestParam(required = false) Boolean isClosed) {

        List<PartyPostDto> posts = partyService.getPartyPostsPaged(page, size, deadline, isClosed);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @Operation(summary = "일행 모집 글 상세 조회", description = "특정 일행 모집 글의 상세 정보를 조회합니다")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PartyPostDetailDto>> getPartyPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user){
        PartyPostDetailDto dto = partyService.getPartyPostDetail(postId, user);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }


    @Operation(summary = "일행 모집 글 작성", description = "특정 테마에 대한 일행 모집 글을 작성합니다")
    @PostMapping("/{themeId}")
    public ResponseEntity<ApiResponse<Long>> createPost(
            @PathVariable Long themeId,
            @RequestBody PartyPostRequest request,
            @AuthenticationPrincipal User user) {
        Long id = partyService.createPartyPost(user.getId(), themeId, request);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    @Operation(summary = "일행 모집 글 삭제", description = "내가 작성한 일행 모집 글을 삭제합니다")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Object>> deletePartyPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        partyService.deletePartyPost(postId, user);
        return ResponseEntity.ok(ApiResponse.success());
    }


    @Operation(summary = "일행 참여 신청", description = "일행 모집 글에 참여를 신청합니다")
    @PostMapping("/{PartyPostId}/join")
    public ResponseEntity<ApiResponse<Object>> joinPost(
            @PathVariable Long PartyPostId,
            @AuthenticationPrincipal User user) {
        partyService.joinParty(PartyPostId, user.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "일행 참여 신청 취소", description = "내가 신청한 일행 참여를 취소합니다")
    @DeleteMapping("/{postId}/join")
    public ResponseEntity<ApiResponse<Object>> cancelJoin(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        partyService.cancelJoin(postId, user.getId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "받은 참여 신청 조회", description = "내가 작성한 모집 글에 대한 참여 신청 목록을 조회합니다 (파티장용)")
    @GetMapping("/join/notification")
    public ResponseEntity<ApiResponse<List<PartyJoinDto>>> getJoinRequestsByWriter(
            @AuthenticationPrincipal User user) {
        List<PartyJoinDto> joins = partyService.getJoinRequestsByWriter(user.getId());
        return ResponseEntity.ok(ApiResponse.success(joins));
    }

    @Operation(summary = "참여 신청 승인/거절", description = "일행 참여 신청을 승인하거나 거절합니다")
    @PostMapping("/join/{joinId}/status")
    public ResponseEntity<ApiResponse<Object>> updateJoinStatus(
            @PathVariable Long joinId,
            @RequestBody PartyJoinRequest request) {
        partyService.updateJoinStatus(joinId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "내 참여 신청 상태 조회", description = "내가 신청한 일행의 승인/거절 상태를 조회합니다")
    @GetMapping("/join/status")
    public ResponseEntity<ApiResponse<List<PartyJoinDto>>> getMyJoinStatus(
            @AuthenticationPrincipal User user) {
        List<PartyJoinDto> joins = partyService.getMyResolvedJoins(user.getId());
        return ResponseEntity.ok(ApiResponse.success(joins));
    }

}


