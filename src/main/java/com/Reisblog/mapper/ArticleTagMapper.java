package com.Reisblog.mapper;

import com.Reisblog.entity.ArticleTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleTagMapper{
    // 可选：自定义查询，直接获取标签名列表（避免多次查询）
    @Select("SELECT t.name FROM tag t INNER JOIN article_tag at ON t.id = at.tag_id WHERE at.article_id = #{articleId}")
    List<String> selectTagNamesByArticleId(Long articleId);

    // 插入文章-标签关联
    @Insert("INSERT INTO article_tag(article_id, tag_id) VALUES(#{articleId}, #{tagId})")
    int insert(ArticleTag articleTag);

    // 根据文章ID删除所有关联
    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);
}
