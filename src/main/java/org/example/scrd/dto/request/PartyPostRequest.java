package org.example.scrd.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PartyPostRequest {
    private String title;
    private String content;
    private int currentParticipants;
    private int maxParticipants;
    private LocalDateTime deadline;
}
