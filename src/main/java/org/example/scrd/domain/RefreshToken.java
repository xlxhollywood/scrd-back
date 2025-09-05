package org.example.scrd.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("refreshToken")
@AllArgsConstructor
public class RefreshToken {

    @Id
    private Long userId;
    private String refreshToken;

    @TimeToLive        // 동적으로 만료시간을 세팅
    private Long ttl;  // 초 단위!

}
