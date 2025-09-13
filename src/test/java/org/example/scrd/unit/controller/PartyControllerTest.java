package org.example.scrd.controller;

import org.example.scrd.domain.Role;
import org.example.scrd.domain.Tier;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.PartyJoinRequest;
import org.example.scrd.dto.request.PartyPostRequest;
import org.example.scrd.dto.response.ApiResponse;
import org.example.scrd.dto.response.PartyJoinResponse;
import org.example.scrd.dto.response.PartyPostDetailReseponse;
import org.example.scrd.dto.response.PartyPostResponse;
import org.example.scrd.exception.AlreadyJoinedException;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.exception.PartyClosedException;
import org.example.scrd.exception.UnauthorizedAccessException;
import org.example.scrd.service.PartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PartyController 테스트 클래스
 * 
 * 📚 테스트 목적:
 * - 일행 모집 글 CRUD 기능 테스트
 * - 일행 참여/취소 기능 테스트
 * - 참여 신청 승인/거절 기능 테스트
 * - 권한 검증 테스트
 * - 예외 상황 테스트
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PartyControllerTest {

    @Mock
    private PartyService partyService;

    @InjectMocks
    private PartyController partyController;

    // 테스트에 사용할 샘플 데이터
    private User sampleUser;
    private User adminUser;
    private PartyPostRequest samplePartyPostRequest;
    private PartyJoinRequest samplePartyJoinRequest;
    private PartyPostResponse samplePartyPostResponse;
    private PartyPostDetailReseponse samplePartyPostDetailResponse;
    private PartyJoinResponse samplePartyJoinResponse;

    @BeforeEach
    void setUp() {
        // 일반 사용자 생성
        sampleUser = User.builder()
                .id(1L)
                .kakaoId(12345L)
                .name("테스트사용자")
                .email("test@example.com")
                .nickName("테스트닉네임")
                .tier(Tier.THREE)
                .role(Role.ROLE_USER)
                .build();

        // 관리자 사용자 생성
        adminUser = User.builder()
                .id(2L)
                .kakaoId(67890L)
                .name("관리자")
                .email("admin@example.com")
                .nickName("관리자닉네임")
                .tier(Tier.FIVE)
                .role(Role.ROLE_ADMIN)
                .build();

        // 파티 포스트 요청 DTO 생성 (Builder 패턴 사용)
        samplePartyPostRequest = PartyPostRequest.builder()
                .title("테스트 파티 모집")
                .content("정말 재미있는 테마로 함께 방탈출 해요!")
                .maxParticipants(4)
                .currentParticipants(0)
                .deadline(LocalDate.of(2024, 1, 20).atStartOfDay())
                .build();

        // 파티 조인 요청 DTO 생성 (Builder 패턴 사용)
        samplePartyJoinRequest = PartyJoinRequest.builder()
                .status("APPROVED")
                .build();

        // 파티 포스트 응답 DTO 생성
        samplePartyPostResponse = PartyPostResponse.builder()
                .id(1L)
                .title("테스트 파티 모집")
                .maxParticipants(4)
                .currentParticipants(2)
                .deadline(LocalDate.of(2024, 1, 20).atStartOfDay())
                .isClosed(false)
                .themeTitle("테스트 테마")
                .location("서울시 강남구")
                .image("https://example.com/theme.jpg")
                .build();

        // 파티 포스트 상세 응답 DTO 생성
        samplePartyPostDetailResponse = PartyPostDetailReseponse.builder()
                .postId(1L)
                .title("테스트 파티 모집")
                .content("정말 재미있는 테마로 함께 방탈출 해요!")
                .maxParticipants(4)
                .currentParticipants(2)
                .deadline(LocalDate.of(2024, 1, 20).atStartOfDay())
                .isClosed(false)
                .writerNickname("테스트닉네임")
                .themeTitle("테스트 테마")
                .location("서울시 강남구")
                .build();

        // 파티 조인 응답 DTO 생성
        samplePartyJoinResponse = new PartyJoinResponse(
                1L, // joinId
                1L, // userId
                "테스트닉네임", // username
                "PENDING", // status
                LocalDate.of(2024, 1, 15).atStartOfDay(), // regDate
                1L, // postId
                "테스트 파티 모집" // postTitle
        );
    }

    /**
     * 🧪 테스트 1: 일행 모집 글 목록 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 페이징 기반으로 일행 모집 글 목록 조회
     * 2. PartyService가 모집 글 목록을 반환
     * 3. 200 OK와 함께 모집 글 목록 응답
     */
    @Test
    @DisplayName("일행 모집 글 목록 조회 - 성공")
    void getPartyPostsPaged_Success() {
        // Given
        int page = 0;
        int size = 20;
        LocalDate deadline = LocalDate.of(2024, 1, 20);
        Boolean isClosed = false;
        
        List<PartyPostResponse> expectedPosts = Arrays.asList(samplePartyPostResponse);
        when(partyService.getPartyPostsPaged(page, size, deadline, isClosed)).thenReturn(expectedPosts);

        // When
        ResponseEntity<ApiResponse<List<PartyPostResponse>>> result = 
                partyController.getPartyPostsPaged(page, size, deadline, isClosed);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("테스트 파티 모집", result.getBody().getData().get(0).getTitle());

        verify(partyService).getPartyPostsPaged(page, size, deadline, isClosed);
    }

    /**
     * 🧪 테스트 2: 일행 모집 글 상세 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 특정 모집 글의 상세 정보 조회
     * 2. PartyService가 상세 정보를 반환
     * 3. 200 OK와 함께 상세 정보 응답
     */
    @Test
    @DisplayName("일행 모집 글 상세 조회 - 성공")
    void getPartyPostDetail_Success() {
        // Given
        Long postId = 1L;
        when(partyService.getPartyPostDetail(postId, sampleUser)).thenReturn(samplePartyPostDetailResponse);

        // When
        ResponseEntity<ApiResponse<PartyPostDetailReseponse>> result = 
                partyController.getPartyPostDetail(postId, sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());
        assertEquals("테스트 파티 모집", result.getBody().getData().getTitle());

        verify(partyService).getPartyPostDetail(postId, sampleUser);
    }

    /**
     * 🧪 테스트 3: 일행 모집 글 작성 성공
     * 
     * 테스트 시나리오:
     * 1. 특정 테마에 대한 일행 모집 글 작성
     * 2. PartyService가 모집 글을 저장하고 ID 반환
     * 3. 200 OK와 함께 생성된 글 ID 응답
     */
    @Test
    @DisplayName("일행 모집 글 작성 - 성공")
    void createPost_Success() {
        // Given
        Long themeId = 1L;
        Long expectedPostId = 1L;
        // Mock 설정은 실제 호출 시에만 필요
        
        when(partyService.createPartyPost(sampleUser.getId(), themeId, samplePartyPostRequest)).thenReturn(expectedPostId);

        // When
        ResponseEntity<ApiResponse<Long>> result = 
                partyController.createPost(themeId, samplePartyPostRequest, sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());
        assertEquals(expectedPostId, result.getBody().getData());

        verify(partyService).createPartyPost(sampleUser.getId(), themeId, samplePartyPostRequest);
    }

    /**
     * 🧪 테스트 4: 일행 모집 글 삭제 성공 - 본인 글
     * 
     * 테스트 시나리오:
     * 1. 사용자가 자신이 작성한 모집 글 삭제
     * 2. 권한 검증 통과
     * 3. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("일행 모집 글 삭제 - 본인 글 성공")
    void deletePartyPost_SelfPost_Success() {
        // Given
        Long postId = 1L;
        doNothing().when(partyService).deletePartyPost(postId, sampleUser);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                partyController.deletePartyPost(postId, sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(partyService).deletePartyPost(postId, sampleUser);
    }

    /**
     * 🧪 테스트 5: 일행 모집 글 삭제 성공 - 관리자 권한
     * 
     * 테스트 시나리오:
     * 1. 관리자가 다른 사용자의 모집 글 삭제
     * 2. 관리자 권한으로 삭제 허용
     * 3. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("일행 모집 글 삭제 - 관리자 권한 성공")
    void deletePartyPost_AdminAccess_Success() {
        // Given
        Long postId = 1L;
        doNothing().when(partyService).deletePartyPost(postId, adminUser);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                partyController.deletePartyPost(postId, adminUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(partyService).deletePartyPost(postId, adminUser);
    }

    /**
     * 🧪 테스트 6: 일행 모집 글 삭제 실패 - 권한 없음
     * 
     * 테스트 시나리오:
     * 1. 일반 사용자가 다른 사용자의 모집 글 삭제 시도
     * 2. 권한 검증 실패
     * 3. UnauthorizedAccessException 발생
     */
    @Test
    @DisplayName("일행 모집 글 삭제 - 권한 없음")
    void deletePartyPost_UnauthorizedAccess() {
        // Given
        Long postId = 1L;
        doThrow(new UnauthorizedAccessException())
                .when(partyService).deletePartyPost(postId, sampleUser);

        // When & Then
        assertThrows(UnauthorizedAccessException.class, () -> {
            partyController.deletePartyPost(postId, sampleUser);
        });

        verify(partyService).deletePartyPost(postId, sampleUser);
    }

    /**
     * 🧪 테스트 7: 일행 참여 신청 성공
     * 
     * 테스트 시나리오:
     * 1. 사용자가 일행 모집 글에 참여 신청
     * 2. PartyService가 참여 신청을 처리
     * 3. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("일행 참여 신청 - 성공")
    void joinPost_Success() {
        // Given
        Long partyPostId = 1L;
        doNothing().when(partyService).joinParty(partyPostId, sampleUser.getId());

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                partyController.joinPost(partyPostId, sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(partyService).joinParty(partyPostId, sampleUser.getId());
    }

    /**
     * 🧪 테스트 8: 일행 참여 신청 실패 - 이미 참여함
     * 
     * 테스트 시나리오:
     * 1. 이미 참여한 모집 글에 다시 참여 신청 시도
     * 2. AlreadyJoinedException 발생
     * 3. 예외가 전파됨
     */
    @Test
    @DisplayName("일행 참여 신청 - 이미 참여함")
    void joinPost_AlreadyJoined() {
        // Given
        Long partyPostId = 1L;
        doThrow(new AlreadyJoinedException())
                .when(partyService).joinParty(partyPostId, sampleUser.getId());

        // When & Then
        assertThrows(AlreadyJoinedException.class, () -> {
            partyController.joinPost(partyPostId, sampleUser);
        });

        verify(partyService).joinParty(partyPostId, sampleUser.getId());
    }

    /**
     * 🧪 테스트 9: 일행 참여 신청 실패 - 모집 마감
     * 
     * 테스트 시나리오:
     * 1. 마감된 모집 글에 참여 신청 시도
     * 2. PartyClosedException 발생
     * 3. 예외가 전파됨
     */
    @Test
    @DisplayName("일행 참여 신청 - 모집 마감")
    void joinPost_PartyClosed() {
        // Given
        Long partyPostId = 1L;
        doThrow(new PartyClosedException())
                .when(partyService).joinParty(partyPostId, sampleUser.getId());

        // When & Then
        assertThrows(PartyClosedException.class, () -> {
            partyController.joinPost(partyPostId, sampleUser);
        });

        verify(partyService).joinParty(partyPostId, sampleUser.getId());
    }

    /**
     * 🧪 테스트 10: 일행 참여 신청 취소 성공
     * 
     * 테스트 시나리오:
     * 1. 사용자가 자신의 참여 신청을 취소
     * 2. PartyService가 참여 취소를 처리
     * 3. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("일행 참여 신청 취소 - 성공")
    void cancelJoin_Success() {
        // Given
        Long postId = 1L;
        doNothing().when(partyService).cancelJoin(postId, sampleUser.getId());

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                partyController.cancelJoin(postId, sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(partyService).cancelJoin(postId, sampleUser.getId());
    }

    /**
     * 🧪 테스트 11: 받은 참여 신청 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 내가 작성한 모집 글에 대한 참여 신청 목록 조회
     * 2. PartyService가 참여 신청 목록을 반환
     * 3. 200 OK와 함께 참여 신청 목록 응답
     */
    @Test
    @DisplayName("받은 참여 신청 조회 - 성공")
    void getJoinRequestsByWriter_Success() {
        // Given
        List<PartyJoinResponse> expectedJoins = Arrays.asList(samplePartyJoinResponse);
        when(partyService.getJoinRequestsByWriter(sampleUser.getId())).thenReturn(expectedJoins);

        // When
        ResponseEntity<ApiResponse<List<PartyJoinResponse>>> result = 
                partyController.getJoinRequestsByWriter(sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("테스트닉네임", result.getBody().getData().get(0).getUsername());

        verify(partyService).getJoinRequestsByWriter(sampleUser.getId());
    }

    /**
     * 🧪 테스트 12: 참여 신청 승인/거절 성공
     * 
     * 테스트 시나리오:
     * 1. 일행 참여 신청을 승인 또는 거절
     * 2. PartyService가 상태를 업데이트
     * 3. 200 OK와 성공 메시지 응답
     */
    @Test
    @DisplayName("참여 신청 승인/거절 - 성공")
    void updateJoinStatus_Success() {
        // Given
        Long joinId = 1L;
        // Mock 설정은 실제 호출 시에만 필요
        doNothing().when(partyService).updateJoinStatus(joinId, "APPROVED");

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                partyController.updateJoinStatus(joinId, samplePartyJoinRequest);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());

        verify(partyService).updateJoinStatus(joinId, "APPROVED");
    }

    /**
     * 🧪 테스트 13: 내 참여 신청 상태 조회 성공
     * 
     * 테스트 시나리오:
     * 1. 내가 신청한 일행의 승인/거절 상태 조회
     * 2. PartyService가 상태 목록을 반환
     * 3. 200 OK와 함께 상태 목록 응답
     */
    @Test
    @DisplayName("내 참여 신청 상태 조회 - 성공")
    void getMyJoinStatus_Success() {
        // Given
        List<PartyJoinResponse> expectedJoins = Arrays.asList(samplePartyJoinResponse);
        when(partyService.getMyResolvedJoins(sampleUser.getId())).thenReturn(expectedJoins);

        // When
        ResponseEntity<ApiResponse<List<PartyJoinResponse>>> result = 
                partyController.getMyJoinStatus(sampleUser);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("PENDING", result.getBody().getData().get(0).getStatus());

        verify(partyService).getMyResolvedJoins(sampleUser.getId());
    }

    /**
     * 🧪 테스트 14: 빈 모집 글 목록 조회
     * 
     * 테스트 시나리오:
     * 1. 모집 글이 없는 경우 목록 조회
     * 2. 빈 리스트 반환
     * 3. 200 OK와 빈 리스트 응답
     */
    @Test
    @DisplayName("빈 모집 글 목록 조회 - 성공")
    void getPartyPostsPaged_EmptyList() {
        // Given
        when(partyService.getPartyPostsPaged(0, 20, null, null)).thenReturn(Arrays.asList());

        // When
        ResponseEntity<ApiResponse<List<PartyPostResponse>>> result = 
                partyController.getPartyPostsPaged(0, 20, null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(200, result.getBody().getCode());
        assertEquals("성공", result.getBody().getMessage());
        assertTrue(result.getBody().getData().isEmpty());

        verify(partyService).getPartyPostsPaged(0, 20, null, null);
    }
}
