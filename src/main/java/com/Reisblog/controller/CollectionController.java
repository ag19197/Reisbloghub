package com.Reisblog.controller;

import com.Reisblog.dto.ArticleIdDTO;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.UpdateVisibilityDTO;
import com.Reisblog.dto.collection.CollectionDTO;
import com.Reisblog.dto.collection.CollectionItemDTO;
import com.Reisblog.dto.collection.PublicCollectionDTO;
import com.Reisblog.service.CollectionService;
import com.Reisblog.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
@Tag(name = "收藏接口")
public class CollectionController {

    private final CollectionService collectionService;
    private final JwtUtils jwtUtils;

    /**
     * 从请求中获取当前登录用户的ID（由拦截器设置）
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @PostMapping
    @Operation(summary = "添加收藏")
    public Result<CollectionDTO> addCollection(@RequestBody ArticleIdDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        CollectionDTO result = collectionService.addCollection(userId, dto.getArticleId());
        return Result.success(result);
    }

    @DeleteMapping("/{articleId}")
    @Operation(summary = "取消收藏")
    public Result<Void> removeCollection(@PathVariable Long articleId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        collectionService.removeCollection(userId, articleId);
        return Result.success();
    }

    @PutMapping("/{articleId}")
    @Operation(summary = "修改收藏可见性")
    public Result<Void> updateVisibility(@PathVariable Long articleId,
                                         @RequestBody UpdateVisibilityDTO dto,
                                         HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        collectionService.updateVisibility(userId, articleId, dto.getIsPublic());
        return Result.success();
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户的收藏列表")
    public Result<PageResult<CollectionItemDTO>> getUserCollections(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean publicOnly,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        PageResult<CollectionItemDTO> result = collectionService.getUserCollections(userId, page, size, publicOnly);
        return Result.success(result);
    }

    @GetMapping("/public/{userId}")
    @Operation(summary = "获取指定用户的公开收藏列表")
    public Result<PageResult<PublicCollectionDTO>> getPublicCollections(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<PublicCollectionDTO> result = collectionService.getPublicCollections(userId, page, size);
        return Result.success(result);
    }

    @GetMapping("/status/{articleId}")
    @Operation(summary = "查询当前用户是否已收藏指定文章")
    public Result<Boolean> getCollectionStatus(@PathVariable Long articleId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        boolean collected = collectionService.isCollected(userId, articleId);
        return Result.success(collected);
    }

// 公开收藏列表接口（用于个人主页）放在另一个 Controller 或单独处理，这里暂不重复
}
