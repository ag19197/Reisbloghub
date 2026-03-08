package com.Reisblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.comment.CommentDTO;
import com.Reisblog.entity.Comment;
import com.Reisblog.mapper.CommentMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.Reisblog.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
    //这个方法负责保存评论，并根据内容是否包含屏蔽词设置状态。
    @Override
    public CommentDTO addComment(Comment comment) {
        // 1. 设置默认值
        if (comment.getParentId() == null) {
            comment.setParentId(0L);  // 0 表示一级评论
        }
        // 2. 设置创建时间（也可以用 MyBatis-Plus 自动填充）
        comment.setCreateTime(LocalDateTime.now());

        // 3. 保存到数据库
        save(comment);

        // 4. 转换为 DTO 返回
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
    public PageResult<Comment> getAdminComments(int page, int size, Integer status, Long articleId) {
        return null;
    }

    @Override
    public void auditComment(Long commentId, Integer status) {

    }

    @Override
    public void deleteComment(Long commentId) {

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
