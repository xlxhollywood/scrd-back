package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.Role;
import org.example.scrd.domain.User;
import org.example.scrd.dto.request.UserProfileUpdateRequest;
import org.example.scrd.dto.response.UserResponse;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.exception.UnauthorizedAccessException;
import org.example.scrd.repo.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public UserResponse getUserInfo(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("해당 유가 존재하지 않습니다."));
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(User currentUser, Long targetUserId) {
        // 관리자이거나 본인일 경우에만 삭제 허용
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        boolean isSelf = currentUser.getId().equals(targetUserId);

        if (!isAdmin && !isSelf) {
            throw new UnauthorizedAccessException();
        }

        userRepository.deleteById(targetUserId);
    }

    @Transactional
    public void updateUserProfile(User user, UserProfileUpdateRequest request) {
        if (user == null || user.getId() == null) throw new IllegalArgumentException("유저 ID가 없습니다.");

        User foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        String newNick = request.getNickName();
        if (newNick == null || newNick.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어 있을 수 없습니다.");
        }
        newNick = newNick.trim();
        if (newNick.length() < 2) {
            throw new IllegalArgumentException("닉네임은 최소 2글자 이상이어야 합니다.");
        }

        boolean duplicate = userRepository.existsByNickName(newNick)
                && !newNick.equals(foundUser.getNickName());
        if (duplicate) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        foundUser.setNickName(newNick);
    }

}
