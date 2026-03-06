package com.Reisblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

//评论
@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long parentId;           // 0 表示一级评论
    private String nickname;
    private String email;
    private String content;
    private Integer status;          // 0-待审核 1-已通过 2-已拦截 3-管理员删除
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}