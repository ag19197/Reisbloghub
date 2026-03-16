package com.Reisblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;          // 接收通知的用户ID
    private String type;          // 通知类型：COMMENT, LIKE, COLLECT, SYSTEM等
    private String content;       // 通知内容
    private Long relatedId;       // 关联的业务ID，如文章ID
    private Boolean isRead;       // 是否已读
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}