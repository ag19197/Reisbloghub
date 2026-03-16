package com.Reisblog.service;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.article.AdminArticleDTO;
import com.Reisblog.dto.article.ArticleDetailDTO;
import com.Reisblog.dto.article.ArticleListItemDTO;
import com.Reisblog.dto.like.LikeResultDTO;
import com.Reisblog.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ArticleService extends IService<Article> {
    /**
     * 分页查询文章列表
     * @param page 页码
     * @param size 每页条数
     * @param categoryId 分类ID（可选）
     * @param tagId 标签ID（可选）
     * @param keyword 搜索关键词（可选）
     * @return 分页结果
     */
    PageResult<ArticleListItemDTO> getArticleList(int page, int size, Long categoryId, Long tagId, String keyword);

    ArticleDetailDTO getArticleDetail(Long id, String ip);

    LikeResultDTO likeArticle(Long articleId, Long userId, String ip);

    PageResult<AdminArticleDTO> getAdminArticles(int page, int size, Integer status, Long categoryId, String keyword);

    /**
     * 新增文章
     * @param article 文章实体（不含 ID）
     * @param tagIds 标签 ID 列表
     * @return 保存后的文章
     */
    Article saveArticle(Article article, List<Long> tagIds);

    /**
     * 更新文章
     * @param article 文章实体（必须包含 ID）
     * @param tagIds 标签 ID 列表
     * @return 更新后的文章
     */
    Article updateArticle(Article article, List<Long> tagIds);

    void updateHotScore(Long articleId);

    List<Article> getHotArticles(int topN);

    void initHotRanking();

    PageResult<ArticleListItemDTO> getUserArticles(Long userId, int page, int size);
}
