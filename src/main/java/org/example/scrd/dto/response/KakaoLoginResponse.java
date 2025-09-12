package org.example.scrd.dto.response;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class KakaoLoginResponse {
    private String name;
    private String profileImageUrl;
    private String email;
}