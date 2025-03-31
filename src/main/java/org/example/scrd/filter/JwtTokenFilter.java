package org.example.scrd.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.User;
import org.example.scrd.exception.DoNotLoginException;
import org.example.scrd.exception.WrongTokenException;
import org.example.scrd.service.AuthService;
import org.example.scrd.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final AuthService authService; // 사용자 정보를 가져오는 서비스
    private final String SECRET_KEY; // JWT 서명 검증에 사용할 비밀 키
    private final JwtUtil jwtUtil; // JWT 관련 유틸리티 클래스

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 특정 경로에 대해 필터를 무시하고 다음 필터로 이동
        if (request.getRequestURI().startsWith("/error") ||
                request.getRequestURI().startsWith("/scrd/auth/") ||
                request.getRequestURI().startsWith("/scrd/every") ||
                request.getRequestURI().equals("/")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 토큰을 가져옴
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Refresh Token 헤더에서 값을 가져옴
        String refreshToken = getRefreshTokenFromHeader(request);

        // Authorization 헤더가 없는 경우 예외 처리
        if (authorizationHeader == null) throw new DoNotLoginException();

        // Authorization 헤더가 "Bearer "로 시작하지 않으면 예외 처리
        if (!authorizationHeader.startsWith("Bearer "))
            throw new WrongTokenException("Bearer 로 시작하지 않는 토큰입니다.");

        // Bearer 이후의 실제 토큰 값 추출
        String token = authorizationHeader.split(" ")[1];

        // Refresh Token이 없는 경우 Access Token만 처리
        if (refreshToken == null) {
            processAccessToken(request, response, filterChain, token);
        } else {
            // Refresh Token이 있는 경우 Access/Refresh Token을 처리
            processRefreshToken(request, response, filterChain, refreshToken);
        }
    }

    /**
     * Refresh Token 헤더에서 값을 추출하는 메서드
     *
     * @param request HttpServletRequest 객체
     * @return Refresh Token 값
     */
    private String getRefreshTokenFromHeader(HttpServletRequest request) {
        String refreshToken = null;
        if (request.getHeader("X-Refresh-Token") != null && !request.getHeader("X-Refresh-Token").isEmpty()) {
            refreshToken = request.getHeader("X-Refresh-Token");
        }
        return refreshToken;
    }

    /**
     * Access Token만 처리하는 메서드
     *
     * @param request     HttpServletRequest 객체
     * @param response    HttpServletResponse 객체
     * @param filterChain 필터 체인
     * @param token       Access Token 값
     * @throws ServletException
     * @throws IOException
     */
    private void processAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String token)
            throws ServletException, IOException {
        // Access Token에서 사용자 정보 추출
        User loginUser = authService.getLoginUser(JwtUtil.getUserId(token, SECRET_KEY));

        // 사용자 인증 설정
        setAuthenticationForUser(request, loginUser);

        // 필터 체인의 다음 단계로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * Refresh Token을 처리하고 Access/Refresh Token을 재발급하는 메서드
     *
     * @param request     HttpServletRequest 객체
     * @param response    HttpServletResponse 객체
     * @param filterChain 필터 체인
     * @param refreshToken Refresh Token 값
     * @throws ServletException
     * @throws IOException
     */
    private void processRefreshToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String refreshToken)
            throws ServletException, IOException {
        // Refresh Token 검증 및 새로운 토큰 발급
        List<String> newTokens = jwtUtil.validateRefreshToken(refreshToken, SECRET_KEY);

        // 응답 헤더에 새로운 Access Token과 Refresh Token 설정
        response.setHeader("Authorization", "Bearer " + newTokens.get(0)); // Access Token
        response.setHeader("X-Refresh-Token", newTokens.get(1));

        // 새로운 Access Token에서 사용자 정보 추출
        User loginUser = authService.getLoginUser(JwtUtil.getUserId(newTokens.get(0), SECRET_KEY));

        // 사용자 인증 설정
        setAuthenticationForUser(request, loginUser);

        // 필터 체인의 다음 단계로 요청을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * SecurityContext에 사용자 인증 정보를 설정하는 메서드
     *
     * @param request HttpServletRequest 객체
     */
    private void setAuthenticationForUser(HttpServletRequest request, User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority(user.getRole().name()))); // 예: ROLE_USER

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
