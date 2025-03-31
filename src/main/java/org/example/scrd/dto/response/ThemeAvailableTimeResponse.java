package org.example.scrd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ThemeAvailableTimeResponse {
    private String dateOfThemeAvailableTime;
    private List<String> availableTime;
}
