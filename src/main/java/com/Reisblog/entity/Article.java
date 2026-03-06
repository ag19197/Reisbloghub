package com.Reisblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

//文章
@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content;          // Markdown 原始内容
    private String contentHtml;      // 渲染后的 HTML
    private Long categoryId;
    private String author;
    private Integer readCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;          // 0-草稿 1-已发布 2-定时发布
    private LocalDateTime scheduledPublishTime;
    private Integer isTop;           // 0-否 1-是
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
