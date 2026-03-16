package com.Reisblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.comment.AdminCommentDTO;
import com.Reisblog.dto.comment.CommentDTO;
import com.Reisblog.entity.Article;
import com.Reisblog.entity.Comment;
import com.Reisblog.entity.Notification;
import com.Reisblog.exception.BusinessException;
import com.Reisblog.mapper.ArticleMapper;
import com.Reisblog.mapper.CommentMapper;
import com.Reisblog.service.CommentService;
import com.Reisblog.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final ArticleMapper articleMapper;
    private final NotificationService notificationService;

    //这个方法负责保存评论，并根据内容是否包含屏蔽词设置状态。
    @Override
    public CommentDTO addComment(Comment comment, Long userId) {
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }
        comment.setCreateTime(LocalDateTime.now());
        save(comment);

        // 获取文章作者ID并发送通知
        Article article = articleMapper.selectById(comment.getArticleId());
        if (article != null && article.getUserId() != null && !article.getUserId().equals(userId)) {
            Notification notification = new Notification();
            notification.setUserId(article.getUserId());
            notification.setType("COMMENT");
            notification.setContent("用户 " + comment.getNickname() + " 评论了你的文章：《" + article.getTitle() + "》");
            notification.setRelatedId(article.getId());
            notification.setIsRead(false);
            notificationService.save(notification);
        }

        return BeanUtil.copyProperties(comment, CommentDTO.class);
    }

    //这个方法查询指定文章下已通过的一级评论，并分页返回。
    @Override
    public PageResult<CommentDTO> getArticleComments(Long articleId, int page, int size) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)      // 指定文章
                .eq(Comment::getStatus, 1)                  // 只取已通过
                .eq(Comment::getParentId, 0)                 // 只取一级评论（parentId = 0）
                .orderByDesc(Comment::getCreateTime);        // 按时间倒序

        // 2. 分页查询
        Page<Comment> commentPage = page(new Page<>(page, size), wrapper);

        // 3. 转换为 DTO 列表
        List<CommentDTO> dtoList = commentPage.getRecords().stream()
                .map(comment -> BeanUtil.copyProperties(comment, CommentDTO.class))
                .collect(Collectors.toList());

        // 4. 返回分页结果
        return new PageResult<>(dtoList, commentPage.getTotal(), size, page);
    }

    @Override
    public PageResult<AdminCommentDTO> getAdminComments(int page, int size, Integer status, Long articleId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        if (articleId != null) {
            wrapper.eq(Comment::getArticleId, articleId);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = page(new Page<>(page, size), wrapper);
        List<AdminCommentDTO> dtoList = commentPage.getRecords().stream()
                .map(comment -> BeanUtil.copyProperties(comment, AdminCommentDTO.class))
                .collect(Collectors.toList());
        // 如果需要文章标题，可以在这里关联查询
        return new PageResult<>(dtoList, commentPage.getTotal(), size, page);
    }

    @Override
    public void auditComment(Long commentId, Integer status) {
        Comment comment = getById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        comment.setStatus(status);
        updateById(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        // 逻辑删除：将状态设为3（管理员删除）
        Comment comment = getById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        comment.setStatus(3);
        updateById(comment);
    }

    @Override
    public boolean saveBatch(Collection<Comment> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Comment> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Comment> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Comment entity) {
        return false;
    }

    @Override
    public Comment getOne(Wrapper<Comment> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Optional<Comment> getOneOpt(Wrapper<Comment> queryWrapper, boolean throwEx) {
        return Optional.empty();
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Comment> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<Comment> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public Class<Comment> getEntityClass() {
        return null;
    }
}
