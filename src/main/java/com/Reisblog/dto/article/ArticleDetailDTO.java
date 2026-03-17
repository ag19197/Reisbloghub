package com.Reisblog.dto.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

//文章详情响应
@Data
public class ArticleDetailDTO {
    private Long id;
    private String title;
    private String contentHtml;
    private Long categoryId;
    private String categoryName;
    private List<String> tags;
    private Integer readCount;
    private Integer likeCount;
    private Integer commentCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Boolean hasLiked;  // 当前IP是否已点赞
    // 新增作者信息字段
    private Long authorId;        // 作者用户ID
    private String authorName;    // 作者昵称
    private String authorAvatar;  // 作者头像（可选）
}