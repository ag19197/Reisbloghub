package com.Reisblog.controller.admin;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.comment.AdminCommentDTO;
import com.Reisblog.service.CommentService;
import com.Reisblog.utils.AdminAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/admin/comments")
@RequiredArgsConstructor
@Tag(name = "后台评论管理接口")
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "评论列表")
    public Result<PageResult<AdminCommentDTO>> listComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long articleId,
            HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        PageResult<AdminCommentDTO> result = commentService.getAdminComments(page, size, status, articleId);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "审核评论")
    public Result<Void> auditComment(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        commentService.auditComment(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        commentService.deleteComment(id);
        return Result.success();
    }
}