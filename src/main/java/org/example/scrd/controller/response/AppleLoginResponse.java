package org.example.scrd.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleLoginResponse {
    private String name;
    private String email;
    private String appleId;  // Apple 고유 식별자
}