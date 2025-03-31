package org.example.scrd.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.scrd.domain.User;
import org.example.scrd.dto.Tier;

@NoArgsConstructor
@Getter
public class UserResponse {
    private String name;
    private String email;
    private String profileImageUrl;
    private Tier tier;
    private String gender;
    private String birth;
    private int point;
    private int count;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.name = user.getName();
        response.email = user.getEmail();
        response.profileImageUrl = user.getProfileImageUrl();
        response.tier = user.getTier();
        response.gender = user.getGender();
        response.birth = user.getBirth();
        response.point = user.getPoint();
        response.count = user.getCount();
        return response;
    }
}
