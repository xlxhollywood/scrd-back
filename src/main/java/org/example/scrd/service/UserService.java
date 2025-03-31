package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.Role;
import org.example.scrd.domain.User;
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

//    @Transactional
//    public void delete(Long userId){
//        userRepository.deleteById(userId);
//
//        String redisKey = "refreshToken:" + userId;
//        System.out.println("redisKey : " + redisKey);
//        redisTemplate.delete(redisKey);
//        System.out.println("사용자 탈퇴 완료!");
//    }

    public void deleteUser(User currentUser, Long targetUserId) {
        // 관리자이거나 본인일 경우에만 삭제 허용
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        boolean isSelf = currentUser.getId().equals(targetUserId);

        if (!isAdmin && !isSelf) {
            throw new UnauthorizedAccessException();
        }

        userRepository.deleteById(targetUserId);
    }

}
