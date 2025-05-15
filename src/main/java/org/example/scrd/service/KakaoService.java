package org.example.scrd.service;

import org.example.scrd.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;

@Service
public class KakaoService {

    @Value("${kakao.api.key.client}") // application properties에서 카카오 클라이언트 ID 주입
    private String clientId;

    public UserDto kakaoLogin(String code , String redirectUri) {
        String accessToken = getAccessToken(code, redirectUri); // 응답 받은 code로부터 accessToken 받아내기
        return getKakaoUserInfo(accessToken); // accessToken으로부터 사용자 정보 알아내기
    }
    // 카카오 OAuth 서버에서 액세스 토큰을 받아오는 메서드
    private String getAccessToken(String code, String redirectUri) {

        // HTTP Header 생성 (application/x-www-form-urlencoded 설정)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성 (카카오 API에 전달할 파라미터 설정)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code"); // 인증 코드 기반의 토큰 요청
        body.add("client_id", clientId); // 카카오 개발자 페이지에서 발급받은 클라이언트 ID
        body.add("redirect_uri", redirectUri); // 리다이렉트 URI
        body.add("code", code); // 카카오 로그인 후 받은 인증 코드

        // HTTP 요청 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);


        // 카카오 서버로 HTTP 요청을 보내고 액세스 토큰을 받아옴
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();

        } catch (Exception e) {
            System.err.println("🔥 [카카오 토큰 발급 실패] message: " + e.getMessage());
            if (e instanceof org.springframework.web.client.HttpClientErrorException httpError) {
                System.err.println("🔥 [카카오 응답 바디] " + httpError.getResponseBodyAsString());
            }
            throw new RuntimeException("카카오 토큰 발급 실패", e);
        }
    }

    // 액세스 토큰을 사용하여 카카오 사용자 정보를 가져오는 메서드
    private UserDto getKakaoUserInfo(String accessToken) {
        HashMap<String, Object> userInfo= new HashMap<String,Object>();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 생성
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        // 카카오 API 서버로 사용자 정보 요청
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoUserInfoRequest,
                    String.class
            );

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // 기존 로직
            Long id = jsonNode.get("id").asLong();
            String email = jsonNode.get("kakao_account").get("email").asText();
            String nickname = jsonNode.get("properties").get("nickname").asText();
            String profileImageUrl = jsonNode.get("properties").get("profile_image").asText();

            return UserDto.builder()
                    .kakaoId(id)
                    .name(nickname)
                    .email(email)
                    .profileImageUrl(profileImageUrl)
                    .build();

        } catch (Exception e) {
            System.err.println("🔥 [카카오 사용자 정보 요청 실패] message: " + e.getMessage());
            if (e instanceof org.springframework.web.client.HttpClientErrorException httpError) {
                System.err.println("🔥 [카카오 응답 바디] " + httpError.getResponseBodyAsString());
            }
            throw new RuntimeException("카카오 사용자 정보 요청 실패", e);
        }
    }
}
