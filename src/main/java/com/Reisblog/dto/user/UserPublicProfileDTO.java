package com.Reisblog.dto.user;

import lombok.Data;

@Data
public class UserPublicProfileDTO {
    private Long userId;
    private String nickname;
    private String avatar;
    private Integer followerCount;  // 粉丝数
    private Integer followingCount; // 关注数
    private Integer articleCount;   // 文章数
}