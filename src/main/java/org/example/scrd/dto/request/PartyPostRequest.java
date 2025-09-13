package org.example.scrd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyPostRequest {
    private String title;
    private String content;
    private int currentParticipants;
    private int maxParticipants;
    private LocalDateTime deadline;
}
