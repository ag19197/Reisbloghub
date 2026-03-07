package com.Reisblog.mapper;

import com.Reisblog.entity.Article;
import com.Reisblog.entity.ArticleTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleTagMapper{
    // 可选：自定义查询，直接获取标签名列表（避免多次查询）
    @Select("SELECT t.name FROM tag t INNER JOIN article_tag at ON t.id = at.tag_id WHERE at.article_id = #{articleId}")
    List<String> selectTagNamesByArticleId(Long articleId);
}
