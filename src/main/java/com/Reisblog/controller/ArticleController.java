package com.Reisblog.controller;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.article.ArticleDetailDTO;
import com.Reisblog.dto.article.ArticleListItemDTO;
import com.Reisblog.dto.article.ArticlePublishDTO;
import com.Reisblog.dto.like.LikeResultDTO;
import com.Reisblog.entity.Article;
import com.Reisblog.entity.User;
import com.Reisblog.mapper.CategoryMapper;
import com.Reisblog.service.ArticleService;
import com.Reisblog.service.UserService;
import com.Reisblog.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Tag(name = "前台文章接口")
public class ArticleController {
    private final ArticleService articleService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private final CategoryMapper categoryMapper;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "分页获取文章列表")
    public Result<PageResult<ArticleListItemDTO>> getArticleList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword) {
        PageResult<ArticleListItemDTO> pageResult = articleService.getArticleList(page, size, categoryId, tagId, keyword);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情")
    public Result<ArticleDetailDTO> getArticleDetail(@PathVariable Long id, HttpServletRequest request) {
        String ip = IpUtils.getClientIp(request);
        ArticleDetailDTO detail = articleService.getArticleDetail(id, ip);
        return Result.success(detail);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞/取消点赞")
    public Result<LikeResultDTO> likeArticle(@PathVariable Long id, HttpServletRequest request) {
        String ip = IpUtils.getClientIp(request);
        LikeResultDTO result = articleService.likeArticle(id, ip);
        return Result.success(result);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门文章列表")
    public Result<List<Article>> getHotArticles(@RequestParam(value = "top", defaultValue = "10") int top) {
        System.out.println("Controller: 接收到热门文章请求，top=" + top);
        List<Article> hotList = articleService.getHotArticles(top);
        return Result.success(hotList);
    }

    @PostMapping
    @Operation(summary = "普通用户发布文章")
    public Result<Article> createArticle(@RequestBody ArticlePublishDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.fail("未登录");
        }
        // 将 DTO 转换为 Article 实体
        Article article = BeanUtil.copyProperties(dto, Article.class);
        // 设置作者
        User user = userService.getById(userId);
        article.setAuthor(user.getNickname());
        // 设置默认值
        article.setReadCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setIsTop(0);
        // 保存文章并处理标签
        articleService.saveArticle(article, dto.getTagIds());
        return Result.success(article);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户发布的文章")
    public Result<PageResult<ArticleListItemDTO>> getMyArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        // 从请求中获取当前用户ID（由JWT拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.fail("未登录");
        }
        PageResult<ArticleListItemDTO> result = articleService.getUserArticles(userId, page, size);
        return Result.success(result);
    }

}
