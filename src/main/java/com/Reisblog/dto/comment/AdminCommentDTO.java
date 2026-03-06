package com.Reisblog.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

// 后台评论列表项
@Data
public class AdminCommentDTO {
    private Long id;
    private Long articleId;
    private String articleTitle;
    private Long parentId;
    private String nickname;
    private String email;
    private String content;
    private Integer status;  // 0待审 1通过 2拦截 3删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
