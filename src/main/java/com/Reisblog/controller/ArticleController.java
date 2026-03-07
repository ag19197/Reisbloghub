package com.Reisblog.controller;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.article.ArticleListItemDTO;
import com.Reisblog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
}
