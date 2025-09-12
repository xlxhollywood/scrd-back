package org.example.scrd.dto;

import lombok.Builder;
import org.example.scrd.domain.Theme;

import java.io.Serializable;
import java.util.List;

public class MobileThemeDto extends ThemeDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> availableTimes;

    @Builder(builderMethodName = "mobileBuilder")
    public MobileThemeDto(Theme theme, List<String> availableTimes) {
        super(
                theme.getId(),
                theme.getTitle(),
                theme.getDescription(),
                theme.getLocation(),
                theme.getPrice(),
                theme.getImage(),
                theme.getUrl(),
                theme.getBrand(),
                theme.getBranch(),
                theme.getPlaytime(),
                theme.getRating(),
                theme.getProportion(),
                theme.getHorror(),
                theme.getActivity(),
                theme.getLevel(),
                theme.getReviewCount()
        );
        this.availableTimes = availableTimes;
    }

    public static MobileThemeDto from(Theme theme, List<String> availableTimes) {
        return MobileThemeDto.mobileBuilder()
                .theme(theme)
                .availableTimes(availableTimes)
                .build();
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }
}
