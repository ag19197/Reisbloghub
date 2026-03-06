package com.Reisblog.dto.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

// 后台文章列表项
@Data
public class AdminArticleDTO {
    private Long id;
    private String title;
    private String summary;
    private String categoryName;
    private Integer readCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;          // 0草稿 1发布 2定时
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledPublishTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
