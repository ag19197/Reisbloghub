package com.Reisblog.dto.article;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticlePublishDTO {
    private String title;
    private String summary;
    private String content;
    private Long categoryId;
    private List<Long> tagIds;
    private Integer status; // 0草稿 1发布 2定时
    private LocalDateTime scheduledPublishTime;
}