package com.Reisblog.dto.collection;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

//公开收藏夹列表项
@Data
public class PublicCollectionDTO {
    private Long articleId;
    private String articleTitle;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
