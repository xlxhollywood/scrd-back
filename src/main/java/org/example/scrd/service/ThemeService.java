package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.ThemeDocument;
import org.example.scrd.dto.LocationCountDto;
import org.example.scrd.dto.MobileThemeDto;
import org.example.scrd.dto.ThemeDto;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.repo.ThemeMongoRepository;
import org.example.scrd.repo.ThemeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final ThemeMongoRepository themeMongoRepository;

    public void addTheme(ThemeDto dto){
        themeRepository.save(Theme.from(dto));
    }

    @Transactional
    public void updateTheme(Long themeId, ThemeDto dto) {
        Theme theme =
                themeRepository.findById(themeId).orElseThrow(() -> new NotFoundException("방탈출 주제가 존재하지 않습니다."));
        theme.update(dto);
    }

    public Theme getThemeById(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("해당 테마가 없습니다."));
    }

    //DB에서 모든 Theme 엔티티를 꺼내서, 각 항목을 ThemeDto로 변환한 다음, 그걸 리스트로 만들어서 반환한다.
    public List<ThemeDto> getAllThemes() {
        List<Theme> themes = themeRepository.findAll();
        return themes
            .stream()
            .map(ThemeDto::toDto)
            .collect(Collectors.toList());
    }


    public List<String> getAvailableTimesByDate(Long themeId, String date) {
        return themeMongoRepository.findByThemeIdAndDate(themeId.intValue(), date)
                .orElseThrow(() -> new RuntimeException("해당 테마와 날짜에 맞는 도큐먼트가 없습니다."))
                .getAvailableTimes();
    }

    public List<ThemeDto> getThemesSortedByRating() {
        return themeRepository.findThemesOrderByReviewCountAndRating().stream()
                .map(ThemeDto::toDto)
                .collect(Collectors.toList());
    }

    public List<MobileThemeDto> getThemesByFilterCriteria(
            String keyword,
            Integer horror,
            Integer activity,
            Float levelMin,
            Float levelMax,
            String location,
            LocalDate date,
            int page,
            int size,
            String sort
    ) {

        List<Theme> themes = themeRepository.findThemesByCriteria(
                keyword, horror, activity, levelMin, levelMax, location, page, size, sort
        );

        String dateString = date != null ? date.toString() : LocalDate.now().toString();

        List<MobileThemeDto> result = themes.stream().map(theme -> {
            List<String> availableTimes = themeMongoRepository.findByThemeIdAndDate(theme.getId().intValue(), dateString)
                    .map(ThemeDocument::getAvailableTimes)
                    .orElse(Collections.emptyList());
            return MobileThemeDto.from(theme, availableTimes);
        }).collect(Collectors.toList());

        return result;
    }


    public Map<String, Object> getLocationCountsWithTotal() {
        List<LocationCountDto> counts = themeRepository.countThemesByLocation();
        int total = counts.stream()
                .mapToInt(c -> c.getCount().intValue())
                .sum();


        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("counts", counts);
        return result;
    }




}
