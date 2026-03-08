package com.Reisblog.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;

// 点赞结果
@Data
@AllArgsConstructor
public class LikeResultDTO {
    private Integer likeCount;
    private String action;  // "like" 或 "unlike"
}