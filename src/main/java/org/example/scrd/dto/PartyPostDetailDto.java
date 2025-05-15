package org.example.scrd.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.scrd.domain.PartyPost;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyPostDetailDto {

    //  PartyPost 정보
    private Long postId;
    private LocalDateTime regDate;
    private String title;
    private String content;
    private int currentParticipants;
    private int maxParticipants;
    private boolean isClosed;
    private LocalDateTime deadline;

    // 작성자 정보
    private Long writerId;
    private String writerNickname;
    private String writerTier;


    // 테마 정보
    private String themeTitle;
    private String themeImage;
    private int horror;
    private int activity;
    private float level;
    private Integer playTime;
    private String location;
    private String branch;
    private String brand;

    // 유저가 이미 참여한 테마인지 아닌지 알기 위한 필드
    private String joinStatus;

    public static PartyPostDetailDto from(PartyPost post, String joinStatus) {
        Theme theme = post.getTheme();
        User writer = post.getWriter();

        return PartyPostDetailDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .regDate(post.getRegDate())
                .deadline(post.getDeadline())
                .currentParticipants(post.getCurrentParticipants())
                .maxParticipants(post.getMaxParticipants())
                .isClosed(post.isClosed())

                .writerId(writer.getId())
                .writerNickname(writer.getNickName())
                .writerTier(writer.getTier().name())


                .themeTitle(theme.getTitle())
                .themeImage(theme.getImage())
                .horror(theme.getHorror())
                .activity(theme.getActivity())
                .level(theme.getLevel())
                .playTime(theme.getPlaytime())
                .location(theme.getLocation())
                .branch(theme.getBranch())
                .brand(theme.getBrand())

                .joinStatus(joinStatus)
                .build();
    }
}

