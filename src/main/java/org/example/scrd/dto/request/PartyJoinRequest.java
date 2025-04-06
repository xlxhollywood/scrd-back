package org.example.scrd.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartyJoinRequest {
    private String status; // "PENDING", "APPROVED", "REJECTED" 등
}

