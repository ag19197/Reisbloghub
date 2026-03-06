package com.Reisblog.dto.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

//文章列表项数据模型
@Data
public class ArticleListItemDTO {
    private Long id;
    private String title;
    private String summary;
    private String categoryName;
    private List<String> tags;
    private Integer readCount;
    private Integer likeCount;
    private Integer commentCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}