package org.example.scrd.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.scrd.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.security.interfaces.ECPrivateKey;
import java.util.Date;

@Service
public class AppleService {
    
    private static final Logger logger = LoggerFactory.getLogger(AppleService.class);

    @Value("${apple.api.key.team-id}") // 팀 id
    private String APPLE_TEAM_ID;

    @Value("${apple.api.key.key-id}") // 키
    private String APPLE_LOGIN_KEY;

    @Value("${apple.api.key.client-id}") // 번들ID
    private String APPLE_CLIENT_ID;

    @Value("${apple.api.key.redirect-url}") // 리다이렉트 url
    private String APPLE_REDIRECT_URL;

    @Value("${apple.api.key.path}") // 키 경로
    private String APPLE_KEY_PATH;

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com";

    public String getAppleLogin() {
        return APPLE_AUTH_URL + "/auth/authorize"
                + "?client_id=" + APPLE_CLIENT_ID
                + "&redirect_uri=" + APPLE_REDIRECT_URL
                + "&response_type=code%20id_token&scope=name%20email&response_mode=form_post";
    }

    public UserDto getAppleInfo(String code) throws Exception {
        if (code == null)
            throw new Exception("Failed get authorization code");

        String clientSecret = createClientSecret();
        System.out.println("✅ Client Secret 생성 성공");

        String userId = "";
        String email = "";
        String accessToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", APPLE_CLIENT_ID);
            params.add("client_secret", clientSecret);
            params.add("code", code);
            params.add("redirect_uri", APPLE_REDIRECT_URL);

            System.out.println("Apple API 요청 파라미터:");
            System.out.println("client_id: " + APPLE_CLIENT_ID);
            System.out.println("redirect_uri: " + APPLE_REDIRECT_URL);
            System.out.println("code: " + code);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    APPLE_AUTH_URL + "/auth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            System.out.println("Apple API 응답 상태: " + response.getStatusCode());
            System.out.println("Apple API 응답 본문: " + response.getBody());

            // JSON 파싱 수정
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            accessToken = jsonNode.get("access_token").asText();
            String idToken = jsonNode.get("id_token").asText();

            // ID TOKEN 파싱 수정
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet payload = signedJWT.getJWTClaimsSet();

            userId = payload.getSubject();
            email = payload.getStringClaim("email");

        } catch (Exception e) {
            System.out.println("❌ Apple API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("API call failed");
        }

        return UserDto.builder()
                .appleId(userId)
                .email(email)
                .name("Apple User") // 또는 실제 이름 사용
                .build();
    }


    private String createClientSecret() throws Exception {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(APPLE_LOGIN_KEY).build();

        Date now = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(APPLE_TEAM_ID)
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + 3600000))
                .audience(APPLE_AUTH_URL)
                .subject(APPLE_CLIENT_ID)
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(getPrivateKey());
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            ECPrivateKey ecPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey);

            jwt.sign(jwsSigner);
        } catch (Exception e) {
            throw new Exception("Failed create client secret");
        }

        return jwt.serialize();
    }

    private byte[] getPrivateKey() throws Exception {
        try (InputStream input = getClass().getResourceAsStream(APPLE_KEY_PATH)) {
            if (input == null) {
                throw new Exception("Key file not found: " + APPLE_KEY_PATH);
            }

            String pemContent = new String(input.readAllBytes());
            // PEM 헤더/푸터 제거하고 Base64 디코딩
            String privateKeyPEM = pemContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            return java.util.Base64.getDecoder().decode(privateKeyPEM);
        }
    }


}
