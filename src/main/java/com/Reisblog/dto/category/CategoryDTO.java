package com.Reisblog.dto.category;

//分类列表项

import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long articleCount;  // 文章数，可通过额外查询填充
}
