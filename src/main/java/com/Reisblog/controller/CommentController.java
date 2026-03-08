package com.Reisblog.controller;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.comment.CommentDTO;
import com.Reisblog.entity.Comment;
import com.Reisblog.service.CommentService;
import com.Reisblog.utils.SensitiveWordUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "前台评论接口")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "发表评论")
    public Result<CommentDTO> addComment(@RequestBody Comment comment) {
        // 1. 校验必要字段
        if (comment.getArticleId() == null || comment.getNickname() == null || comment.getContent() == null) {
            return Result.fail("文章ID、昵称、内容不能为空");
        }
        // 2. 屏蔽词检查
        if (SensitiveWordUtils.containsSensitiveWord(comment.getContent())) {
            comment.setStatus(2); // 已拦截
        } else {
            // 从系统配置读取是否需要审核，这里简单处理：默认需要审核
            comment.setStatus(0); // 待审核
        }
        // 3. 保存评论
        CommentDTO saved = commentService.addComment(comment);
        return Result.success(saved);
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "获取文章评论列表")
    public Result<PageResult<CommentDTO>> getArticleComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<CommentDTO> result = commentService.getArticleComments(articleId, page, size);
        return Result.success(result);
    }

}
