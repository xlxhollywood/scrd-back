package org.example.scrd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Tier {
    ONE("일단계"),
    TWO("이단계"),
    THREE("삼단계"),
    FOUR("사단계"),
    FIVE("오단계");

    private final String tierE;

    public static Tier from(String feature) {
        for (Tier t : Tier.values()) {
            if (t.tierE.equals(feature)) {
                return t;
            }
        }
        throw new IllegalArgumentException();
    }

    public static Tier getTierByCount(int count) {
        if (count <= 5) {
            return ONE;
        } else if (count <= 10) {
            return TWO;
        } else if (count <= 15) {
            return THREE;
        } else if (count <= 20) {
            return FOUR;
        } else {
            return FIVE;
        }
    }
}
