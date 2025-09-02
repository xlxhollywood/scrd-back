package org.example.scrd.controller.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AppleLoginResponse {
    private String name;
    private String email;
    private String sub; // 애플의 고유 식별자
}
