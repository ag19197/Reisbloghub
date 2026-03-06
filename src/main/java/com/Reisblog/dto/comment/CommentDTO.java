package com.Reisblog.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

// 评论
@Data
public class CommentDTO {
    private Long id;
    private Long articleId;
    private Long parentId;
    private String nickname;
    private String email;          // 后台可能需要，前台可不返回
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 状态通常只用于后台，前台只展示通过的评论，所以可以省略
}
