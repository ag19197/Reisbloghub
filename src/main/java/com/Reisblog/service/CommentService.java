package com.Reisblog.service;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.comment.AdminCommentDTO;
import com.Reisblog.dto.comment.CommentDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.Reisblog.entity.Comment;

public interface CommentService extends IService<Comment> {
    /**
     * 发表评论
     */
    CommentDTO addComment(Comment comment);

    /**
     * 获取文章的评论列表（仅显示已通过）
     */
    PageResult<CommentDTO> getArticleComments(Long articleId, int page, int size);

    /**
     * 管理员获取评论列表（所有状态）
     */
    PageResult<AdminCommentDTO> getAdminComments(int page, int size, Integer status, Long articleId);

    /**
     * 审核评论（修改状态）
     */
    void auditComment(Long commentId, Integer status);

    /**
     * 删除评论（逻辑删除）
     */
    void deleteComment(Long commentId);

}
