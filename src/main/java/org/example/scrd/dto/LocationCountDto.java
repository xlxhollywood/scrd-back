package org.example.scrd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationCountDto {
    private String location;
    private Long count;
}
