package com.Reisblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


//关联表实体，通常用于复杂查询
@Data
@TableName("article_tag")
public class ArticleTag {
    @TableId(type = IdType.AUTO) // 主键自增
    private Long articleId;
    private Long tagId;
}