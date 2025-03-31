package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.SavedTheme;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.dto.response.SavedThemeListResponse;
import org.example.scrd.dto.response.SavedThemeResponse;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.repo.ReviewRepository;
import org.example.scrd.repo.SavedThemeRepository;
import org.example.scrd.repo.ThemeRepository;
import org.example.scrd.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedThemeService {
    private final SavedThemeRepository savedThemeRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public SavedThemeResponse savedUserTheme(Long themeId, Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new NotFoundException("방탈출 정보가 존재하지 않습니다."));
        SavedThemeResponse savedThemeResponse = new SavedThemeResponse();
        if(user.getLikes().stream().anyMatch(like -> like.getTheme().equals(theme))){ // 유저가 이미 이 테마를 좋아요한 상태라면 SavedTheme 삭제 (좋아요 취소 → liked = false 로 응답
            savedThemeRepository.deleteByUserAndTheme(user,theme);
            savedThemeResponse.setSaved(false);
        } else {
            savedThemeRepository.save(SavedTheme.builder().theme(theme).user(user).build()); // 그렇지 않으면: → SavedTheme 새로 저장 (좋아요 추가) liked = true 로 응답
            savedThemeResponse.setSaved(true);
        }
        return savedThemeResponse;
    }
    @Transactional(readOnly = true)
    public List<SavedThemeListResponse> getSavedThemeList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        List<SavedTheme> savedThemes = savedThemeRepository.findByUser(user);

        return savedThemes.stream()
                .map(savedTheme -> SavedThemeListResponse.builder()
                        .themeId(savedTheme.getTheme().getId())
                        .title(savedTheme.getTheme().getTitle())
                        .description(savedTheme.getTheme().getDescription())
                        .image(savedTheme.getTheme().getImage())
                        .brand(savedTheme.getTheme().getBrand())
                        .branch(savedTheme.getTheme().getBranch())
                        .build())
                .toList();
    }

}
