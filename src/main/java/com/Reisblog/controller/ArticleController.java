package com.Reisblog.controller;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.article.ArticleDetailDTO;
import com.Reisblog.dto.article.ArticleListItemDTO;
import com.Reisblog.service.ArticleService;
import com.Reisblog.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Tag(name = "前台文章接口")
public class ArticleController {
    private final ArticleService articleService;

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

}
