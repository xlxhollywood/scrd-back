package org.example.scrd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationCountResponse {
    private String location;
    private Long count;
}
