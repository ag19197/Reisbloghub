package com.Reisblog.dto.like;

import lombok.Data;

// 点赞结果
@Data
public class LikeResultDTO {
    private Integer likeCount;
    private String action;  // "like" 或 "unlike"
}