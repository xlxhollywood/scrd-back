package org.example.scrd.domain;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public String getKey() {
        return name(); // enum에 구현되어 있는 메서드 ROLE_USER을 가져와준다.
    }
}
