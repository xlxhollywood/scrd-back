package org.example.scrd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Tier {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5");

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
