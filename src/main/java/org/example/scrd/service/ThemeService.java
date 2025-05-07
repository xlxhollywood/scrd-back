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
//    테마 id로 예약 시간대를 불러온다. 하지만 한 테마의 일주일 치 시간대를 볼 필요가 없어서 주석처리하였음..
//    public List<String> getThemeAvailableTime(Long themeId) {
//        ThemeDocument document = themeMongoRepository.findByThemeId(themeId.intValue())
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("해당 테마의 시간 정보를 찾을 수 없습니다."));
//
//        return document.getAvailableTimes();
//    }

    public List<String> getAvailableTimesByDate(Long themeId, String date) {
        return themeMongoRepository.findByThemeIdAndDate(themeId.intValue(), date)
                .orElseThrow(() -> new RuntimeException("해당 테마와 날짜에 맞는 도큐먼트가 없습니다."))
                .getAvailableTimes();
    }


    public List<ThemeDto> searchThemes(String keyword) {
        List<Theme> themes = themeRepository.findByTitleContainingOrBrandContaining(keyword, keyword);
        return themes.stream().map(ThemeDto::toWebThemeSearchDto).collect(Collectors.toList());
    }

    public List<ThemeDto> getThemesSortedByRating() {
        return themeRepository.findThemesOrderByReviewCountAndRating().stream()
                .map(ThemeDto::toDto)
                .collect(Collectors.toList());
    }

    public List<ThemeDto> getThemesSortedByRating(int page, int size) {
        return themeRepository.findThemesOrderByReviewCountAndRating(page, size).stream()
                .map(ThemeDto::toDto)
                .collect(Collectors.toList());
    }

    public List<ThemeDto> getAllThemes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return themeRepository.findAll(pageable)
                .stream()
                .map(ThemeDto::toDto)
                .collect(Collectors.toList());
    }



    public List<ThemeDto> filterThemes(
            Integer horror,
            Integer activity,
            Float minLevel,
            Float maxLevel,
            Float minRating,
            Float maxRating,
            String location
    ) {
        log.info("🔍 [ThemeService] 필터링 실행: horror={}, activity={}, minLevel={}, maxLevel={}, minRating={}, maxRating={}, location={}",
                horror, activity, minLevel, maxLevel, minRating, maxRating, location);

        List<Theme> filtered = themeRepository.filterThemes(
                horror, activity, minLevel, maxLevel, minRating, maxRating, location
        );

        log.info("✅ [필터링 결과] 총 {}개의 테마 반환됨", filtered.size());

        return filtered.stream()
                .map(ThemeDto::toDto)
                .collect(Collectors.toList());
    }


    public List<MobileThemeDto> getThemesWithAvailableTime(int page, int size, LocalDate date) {
        List<Theme> themes = themeRepository.findThemesOrderByReviewCountAndRating(page, size);
        String dateString = date.toString();

        return themes.stream().map(theme -> {
            List<String> times = themeMongoRepository.findByThemeIdAndDate(theme.getId().intValue(), dateString)
                    .map(ThemeDocument::getAvailableTimes)
                    .orElse(Collections.emptyList());
            return MobileThemeDto.from(theme, times);
        }).collect(Collectors.toList());
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

    public List<MobileThemeDto> searchThemesWithFilters(
            String keyword, Integer horror, Integer activity, String location, LocalDate date) {

        List<Theme> themes = themeRepository.searchByKeywordAndFilters(keyword, horror, activity, location);
        String dateString = date.toString();

        return themes.stream().map(theme -> {
            List<String> times = themeMongoRepository.findByThemeIdAndDate(theme.getId().intValue(), dateString)
                    .map(ThemeDocument::getAvailableTimes)
                    .orElse(Collections.emptyList());
            return MobileThemeDto.from(theme, times);
        }).collect(Collectors.toList());
    }



}
