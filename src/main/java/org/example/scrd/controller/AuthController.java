package org.example.scrd.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.example.scrd.dto.AppleDto;
import org.example.scrd.util.JwtUtil;
import org.example.scrd.dto.UserDto;
import org.example.scrd.controller.response.KakaoLoginResponse;
import org.example.scrd.controller.response.AppleLoginResponse;
import org.example.scrd.service.AuthService;
import org.example.scrd.service.KakaoService;
import org.example.scrd.service.AppleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; // 사용자 인증 관련 서비스
    private final KakaoService kakaoService; // 카카오 API와 통신하는 서비스
    private final AppleService appleService; // 애플 API와 통신하는 서비스
    private final JwtUtil jwtUtil;

    @Value("${custom.jwt.secret}") // application properties에서 JWT 비밀키를 주입받음
    private String SECRET_KEY;

    @Value("${custom.jwt.expire-time-ms}") // JWT 만료 시간을 주입받음
    private long EXPIRE_TIME_MS;
    @Value("${custom.jwt.refresh-expire-time-ms}") // JWT 만료 시간을 주입받음
    private long EXPIRE_REFRESH_TIME_MS;

    @GetMapping("/scrd/auth/kakao-login")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(
            @RequestParam String code,
            HttpServletRequest request,
            HttpServletResponse response) { // HttpServletResponse 추가

        UserDto userDto =
                authService.kakaoLogin(
                        kakaoService.kakaoLogin(code, request.getHeader("Origin") + "/login/oauth/kakao"));

        // JWT 토큰 생성
        List<String> jwtToken = jwtUtil.createToken(userDto.getId(), SECRET_KEY, EXPIRE_TIME_MS, EXPIRE_REFRESH_TIME_MS);

        // TODO: 액세스 토큰을 Authorization 헤더, X-Refresh-Token 헤더에 추가
        response.setHeader("Authorization", "Bearer " + jwtToken.get(0));
        response.setHeader("X-Refresh-Token",  jwtToken.get(1));

        // 응답 본문에 JWT 토큰 및 사용자 정보 추가
        return ResponseEntity.ok(
                KakaoLoginResponse.builder()
                        .name(userDto.getName())
                        .profileImageUrl(userDto.getProfileImageUrl())
                        .email(userDto.getEmail())
                        .build());
    }

    @PostMapping("/scrd/auth/apple-login")
    public ResponseEntity<AppleLoginResponse> appleLogin(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String id_token,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            // 1. AppleService를 통해 Apple에서 사용자 정보 가져오기
            UserDto appleUserInfo = appleService.getAppleInfo(code);

            // 2. user 파라미터에서 이름 추출 (첫 로그인 시)
            String userName = "Apple User"; // 기본값
            if (user != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode userInfo = mapper.readTree(user);
                String firstName = userInfo.path("name").path("firstName").asText();
                String lastName = userInfo.path("name").path("lastName").asText();
                userName = firstName + " " + lastName;
            }

            // UserDto에 이름 설정
            appleUserInfo = UserDto.builder()
                    .appleId(appleUserInfo.getAppleId())
                    .email(appleUserInfo.getEmail())
                    .name(userName) // 추출한 이름 사용
                    .build();

            // 2. AuthService를 통해 로그인 처리 (DB 저장/조회)
            UserDto loginUser = authService.appleLogin(appleUserInfo);

            // 3. JWT 토큰 생성
            List<String> jwtToken = jwtUtil.createToken(loginUser.getId(), SECRET_KEY, EXPIRE_TIME_MS, EXPIRE_REFRESH_TIME_MS);

            // 4. 헤더에 토큰 설정
            response.setHeader("Authorization", "Bearer " + jwtToken.get(0));
            response.setHeader("X-Refresh-Token", jwtToken.get(1));


            return ResponseEntity.ok(
                    AppleLoginResponse.builder()
                            .name(loginUser.getName())
                            .email(loginUser.getEmail())
                            .appleId(loginUser.getAppleId())
                            .build());

        } catch (Exception e) {
            System.out.println("❌ Apple login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
