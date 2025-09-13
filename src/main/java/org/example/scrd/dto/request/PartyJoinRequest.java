package org.example.scrd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyJoinRequest {
    private String status; // "PENDING", "APPROVED", "REJECTED" 등
}

