package org.example.scrd.service;


import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.User;
import org.example.scrd.dto.AppleDto;
import org.example.scrd.dto.Tier;
import org.example.scrd.dto.UserDto;
import org.example.scrd.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RandomNicknameService randomNicknameService;

    // 카카오 로그인 로직
    public UserDto kakaoLogin(UserDto dto) {
        User user = userRepository
                .findByKakaoId(dto.getKakaoId())
                .orElseGet(() -> {
                    User newUser = User.from(dto);
                    newUser.setNickName(randomNicknameService.generateUniqueNickname());
                    newUser.setTier(Tier.ONE);
                    return userRepository.save(newUser);
                });

        user.setEmail(dto.getEmail());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        user.setName(dto.getName());

        // 👇 기존 유저인데 닉네임이 없는 경우
        if (user.getNickName() == null || user.getNickName().isBlank()) {
            user.setNickName(randomNicknameService.generateUniqueNickname());
            userRepository.save(user); // 👈 변경 즉시 DB에 반영
        }


        return UserDto.from(user);
    }

    // Apple 로그인 로직
    public UserDto appleLogin(UserDto dto) {
        User user = userRepository
                .findByAppleId(dto.getAppleId())  // AppleDto의 getId() 사용
                .orElseGet(() -> {
                    User newUser = User.from(dto);
                    newUser.setNickName(randomNicknameService.generateUniqueNickname());
                    newUser.setTier(Tier.ONE);
                    return userRepository.save(newUser);
                });

        // Apple에서 받은 정보로 업데이트
        user.setEmail(dto.getEmail());
        user.setAppleId(dto.getAppleId()); // Apple ID 설정

        // 기존 유저인데 닉네임이 없는 경우
        if (user.getNickName() == null || user.getNickName().isBlank()) {
            user.setNickName(randomNicknameService.generateUniqueNickname());
            userRepository.save(user);
        }

        return UserDto.from(user);
    }

    // 사용자 ID로 로그인한 사용자 정보 조회
    public User getLoginUser(Long userId) {
        // 사용자 ID로 사용자를 조회, 없으면 예외 발생
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
    }
}

