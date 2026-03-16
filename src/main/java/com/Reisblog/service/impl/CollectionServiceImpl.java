package com.Reisblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.collection.CollectionDTO;
import com.Reisblog.dto.collection.CollectionItemDTO;
import com.Reisblog.dto.collection.PublicCollectionDTO;
import com.Reisblog.entity.Article;
import com.Reisblog.entity.Collection;
import com.Reisblog.exception.BusinessException;
import com.Reisblog.mapper.ArticleMapper;
import com.Reisblog.mapper.CollectionMapper;
import com.Reisblog.service.CollectionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {

    private final ArticleMapper articleMapper;

    // 添加收藏
    @Override
    @Transactional
    public CollectionDTO addCollection(Long userId, Long articleId) {
        // 检查是否已收藏
        if (isCollected(userId, articleId)) {
            throw new BusinessException("已经收藏过了");
        }
        // 1. 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        // 2. 检查是否已收藏
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getArticleId, articleId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("已经收藏过该文章");
        }
        // 3. 创建收藏记录（默认私密）
        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setArticleId(articleId);
        collection.setIsPublic(0); // 私密
        this.save(collection);
        return BeanUtil.copyProperties(collection, CollectionDTO.class);
    }

    // 移除收藏
    @Override
    @Transactional
    public void removeCollection(Long userId, Long articleId) {
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getArticleId, articleId);
        boolean removed = this.remove(wrapper);
        if (!removed) {
            throw new BusinessException("收藏不存在");
        }
    }

    // 更新收藏可见性
    @Override
    public void updateVisibility(Long userId, Long articleId, Boolean isPublic) {
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getArticleId, articleId);
        Collection collection = this.getOne(wrapper);
        if (collection == null) {
            throw new BusinessException("收藏记录不存在");
        }
        collection.setIsPublic(isPublic ? 1 : 0);
        this.updateById(collection);
    }

    // 获取用户收藏
    @Override
    public PageResult<CollectionItemDTO> getUserCollections(Long userId, int page, int size, Boolean publicOnly) {
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .orderByDesc(Collection::getCreateTime);
        if (publicOnly != null) {
            wrapper.eq(Collection::getIsPublic, publicOnly ? 1 : 0);
        }
        Page<Collection> collectionPage = this.page(new Page<>(page, size), wrapper);
        List<CollectionItemDTO> dtoList = collectionPage.getRecords().stream().map(collection -> {
            CollectionItemDTO dto = BeanUtil.copyProperties(collection, CollectionItemDTO.class);
            // 查询文章标题
            Article article = articleMapper.selectById(collection.getArticleId());
            if (article != null) {
                dto.setArticleTitle(article.getTitle());
            }
            return dto;
        }).collect(Collectors.toList());
        return new PageResult<>(dtoList, collectionPage.getTotal(), size, page);
    }

    // 获取公开收藏
    @Override
    public PageResult<PublicCollectionDTO> getPublicCollections(Long userId, int page, int size) {
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getIsPublic, 1)
                .orderByDesc(Collection::getCreateTime);
        Page<Collection> collectionPage = this.page(new Page<>(page, size), wrapper);
        List<PublicCollectionDTO> dtoList = collectionPage.getRecords().stream().map(collection -> {
            PublicCollectionDTO dto = new PublicCollectionDTO();
            dto.setArticleId(collection.getArticleId());
            // 查询文章标题
            Article article = articleMapper.selectById(collection.getArticleId());
            if (article != null) {
                dto.setArticleTitle(article.getTitle());
            }
            dto.setCreateTime(collection.getCreateTime());
            return dto;
        }).collect(Collectors.toList());
        return new PageResult<>(dtoList, collectionPage.getTotal(), size, page);
    }

    @Override
    public boolean isCollected(Long userId, Long articleId) {
        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getArticleId, articleId);
        return this.count(wrapper) > 0;
    }

}
