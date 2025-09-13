package org.example.scrd.exception;

import org.example.scrd.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GlobalExceptionHandler 테스트 클래스
 * 
 * 📚 테스트 목적:
 * - 전역 예외 처리기가 올바르게 동작하는지 확인
 * - 각 예외 타입별로 적절한 HTTP 상태 코드와 메시지가 반환되는지 확인
 * - ApiResponse 형식으로 응답이 변환되는지 확인
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    /**
     * 🧪 테스트 1: NotFoundException 처리
     * 
     * 예상 결과:
     * - HTTP 상태 코드: 404 NOT_FOUND
     * - 응답 본문: ApiResponse.error(404, "해당 유가 존재하지 않습니다.")
     */
    @Test
    @DisplayName("NotFoundException 처리 - 404 응답")
    void handleNotFound_Returns404() {
        // Given
        String errorMessage = "해당 유가 존재하지 않습니다.";
        NotFoundException exception = new NotFoundException(errorMessage);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                globalExceptionHandler.handleNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(404, result.getBody().getCode());
        assertEquals(errorMessage, result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    /**
     * 🧪 테스트 2: UnauthorizedAccessException 처리
     * 
     * 예상 결과:
     * - HTTP 상태 코드: 403 FORBIDDEN
     * - 응답 본문: ApiResponse.error(403, "접근 권한이 없습니다.")
     */
    @Test
    @DisplayName("UnauthorizedAccessException 처리 - 403 응답")
    void handleUnauthorizedAccess_Returns403() {
        // Given
        UnauthorizedAccessException exception = new UnauthorizedAccessException();

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                globalExceptionHandler.handleUnauthorizedAccess(exception);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(403, result.getBody().getCode());
        assertEquals("접근 권한이 없습니다.", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    /**
     * 🧪 테스트 3: AlreadyJoinedException 처리
     * 
     * 예상 결과:
     * - HTTP 상태 코드: 400 BAD_REQUEST
     * - 응답 본문: ApiResponse.error(400, "이미 신청한 일행입니다.")
     */
    @Test
    @DisplayName("AlreadyJoinedException 처리 - 400 응답")
    void handleAlreadyJoined_Returns400() {
        // Given
        AlreadyJoinedException exception = new AlreadyJoinedException();

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                globalExceptionHandler.handleBusinessLogic(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(400, result.getBody().getCode());
        assertEquals("이미 신청한 일행입니다.", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    /**
     * 🧪 테스트 4: PartyClosedException 처리
     * 
     * 예상 결과:
     * - HTTP 상태 코드: 400 BAD_REQUEST
     * - 응답 본문: ApiResponse.error(400, "모집이 마감된 일행입니다.")
     */
    @Test
    @DisplayName("PartyClosedException 처리 - 400 응답")
    void handlePartyClosed_Returns400() {
        // Given
        PartyClosedException exception = new PartyClosedException();

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                globalExceptionHandler.handleBusinessLogic(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(400, result.getBody().getCode());
        assertEquals("모집이 마감된 일행입니다.", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    /**
     * 🧪 테스트 5: IllegalArgumentException 처리
     * 
     * 예상 결과:
     * - HTTP 상태 코드: 400 BAD_REQUEST
     * - 응답 본문: ApiResponse.error(400, "잘못된 인수입니다.")
     */
    @Test
    @DisplayName("IllegalArgumentException 처리 - 400 응답")
    void handleIllegalArgument_Returns400() {
        // Given
        String errorMessage = "잘못된 인수입니다.";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                globalExceptionHandler.handleGeneral(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(400, result.getBody().getCode());
        assertEquals(errorMessage, result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    /**
     * 🧪 테스트 6: IllegalStateException 처리
     * 
     * 예상 결과:
     * - HTTP 상태 코드: 400 BAD_REQUEST
     * - 응답 본문: ApiResponse.error(400, "잘못된 상태입니다.")
     */
    @Test
    @DisplayName("IllegalStateException 처리 - 400 응답")
    void handleIllegalState_Returns400() {
        // Given
        String errorMessage = "잘못된 상태입니다.";
        IllegalStateException exception = new IllegalStateException(errorMessage);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                globalExceptionHandler.handleGeneral(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(400, result.getBody().getCode());
        assertEquals(errorMessage, result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    /**
     * 🧪 테스트 7: 일반 Exception 처리 (예상치 못한 오류)
     * 
     * 예상 결과:
     * - HTTP 상태 코드: 500 INTERNAL_SERVER_ERROR
     * - 응답 본문: ApiResponse.error(500, "서버 내부 오류가 발생했습니다.")
     */
    @Test
    @DisplayName("일반 Exception 처리 - 500 응답")
    void handleGeneral_Returns500() {
        // Given
        String errorMessage = "예상치 못한 오류";
        Exception exception = new Exception(errorMessage);

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                globalExceptionHandler.handleUnexpected(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(500, result.getBody().getCode());
        assertEquals("서버 내부 오류가 발생했습니다.", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    /**
     * 🧪 테스트 8: ApiResponse 구조 검증
     * 
     * 예상 결과:
     * - code, message, data 필드가 올바르게 설정됨
     * - JSON 직렬화 가능한 구조
     */
    @Test
    @DisplayName("ApiResponse 구조 검증")
    void apiResponseStructure_IsValid() {
        // Given
        NotFoundException exception = new NotFoundException("테스트 메시지");

        // When
        ResponseEntity<ApiResponse<Object>> result = 
                globalExceptionHandler.handleNotFound(exception);

        // Then
        ApiResponse<Object> response = result.getBody();
        assertNotNull(response);
        
        // 필드 검증
        assertEquals(404, response.getCode());
        assertEquals("테스트 메시지", response.getMessage());
        assertNull(response.getData());
        
        // 타입 검증
        assertTrue(response instanceof ApiResponse);
    }
}
