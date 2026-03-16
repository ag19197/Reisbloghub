package com.Reisblog.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.article.AdminArticleDTO;
import com.Reisblog.entity.Article;
import com.Reisblog.entity.Notification;
import com.Reisblog.service.ArticleService;
import com.Reisblog.service.NotificationService;
import com.Reisblog.utils.AdminAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/admin/articles")
@RequiredArgsConstructor
@Tag(name = "后台文章管理接口")
public class AdminArticleController {

    private final ArticleService articleService;
    private final NotificationService notificationService;


    @GetMapping
    @Operation(summary = "分页获取文章列表（后台）")
    public Result<PageResult<AdminArticleDTO>> listArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        // 权限检查
        AdminAuthUtil.getAdminId(request);
        // 调用service方法
        PageResult<AdminArticleDTO> result = articleService.getAdminArticles(page, size, status, categoryId, keyword);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情（后台）")
    public Result<Article> getArticleById(@PathVariable Long id, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        Article article = articleService.getById(id);
        return Result.success(article);
    }

    @PostMapping
    @Operation(summary = "新增文章")
    public Result<Article> addArticle(@RequestBody AdminArticleDTO dto, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request); // 验证管理员权限

        // 将 DTO 转换为实体
        Article article = BeanUtil.copyProperties(dto, Article.class);
        // 处理定时发布时间（如果有）
        if (dto.getStatus() == 2 && dto.getScheduledPublishTime() != null) {
            article.setScheduledPublishTime(dto.getScheduledPublishTime());
        }
        // 保存文章
        Article saved = articleService.saveArticle(article, dto.getTagIds());
        return Result.success(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改文章")
    public Result<Article> updateArticle(@PathVariable Long id, @RequestBody AdminArticleDTO dto, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);

        Article article = BeanUtil.copyProperties(dto, Article.class);
        article.setId(id);
        Article updated = articleService.updateArticle(article, dto.getTagIds());
        // 在更新文章成功后，如果状态变为已发布（status=1），且原状态不是1，则发送通知
        Article existing = articleService.getById(id);
        if (existing != null && existing.getStatus() != 1 && dto.getStatus() == 1) {
            Notification notification = new Notification();
            notification.setUserId(existing.getUserId());
            notification.setType("ARTICLE_PUBLISHED");
            notification.setContent("你的文章《" + existing.getTitle() + "》已通过审核并发布！");
            notification.setRelatedId(existing.getId());
            notification.setIsRead(false);
            notificationService.save(notification);
        }
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    public Result<Void> deleteArticle(@PathVariable Long id, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        articleService.removeById(id);
        return Result.success();
    }
}