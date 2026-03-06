package com.Reisblog.dto.category;

import lombok.Data;

//标签
@Data
public class TagDTO {
    private Long id;
    private String name;
    private Long articleCount;
}