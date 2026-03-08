package com.Reisblog.service;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.article.ArticleDetailDTO;
import com.Reisblog.dto.article.ArticleListItemDTO;
import com.Reisblog.dto.like.LikeResultDTO;
import com.Reisblog.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

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

    LikeResultDTO likeArticle(Long id, String ip);
}
