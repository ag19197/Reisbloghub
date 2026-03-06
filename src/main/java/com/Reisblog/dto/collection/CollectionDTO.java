package com.Reisblog.dto.collection;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

// 收藏对象 添加时返回
@Data
public class CollectionDTO {
    private Long id;
    private Long articleId;
    private Boolean isPublic;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
