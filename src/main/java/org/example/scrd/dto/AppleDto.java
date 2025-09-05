package org.example.scrd.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
@Data
public class AppleDto {

    private String id;
    private String token;
    private String email;

}
