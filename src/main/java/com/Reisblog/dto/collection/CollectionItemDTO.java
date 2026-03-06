package com.Reisblog.dto.collection;

//当前用户收藏的文章列表项
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CollectionItemDTO {
    private Long collectionId;
    private Long articleId;
    private String articleTitle;
    private Boolean isPublic;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}