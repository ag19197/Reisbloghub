package com.Reisblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

//分类

@Data
@TableName("category")  // 指定表名
public class Category {
    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;
    private String name;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}