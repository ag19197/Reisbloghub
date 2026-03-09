package com.Reisblog.service;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.collection.CollectionDTO;
import com.Reisblog.dto.collection.CollectionItemDTO;
import com.Reisblog.dto.collection.PublicCollectionDTO;
import com.Reisblog.entity.Collection;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CollectionService extends IService<Collection> {
    /**
     * 添加收藏（默认私密）
     */
    CollectionDTO addCollection(Long userId, Long articleId);

    /**
     * 取消收藏
     */
    void removeCollection(Long userId, Long articleId);

    /**
     * 修改收藏可见性
     */
    void updateVisibility(Long userId, Long articleId, Boolean isPublic);

    /**
     * 获取当前用户的收藏列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param publicOnly true则只返回公开收藏
     */
    PageResult<CollectionItemDTO> getUserCollections(Long userId, int page, int size, Boolean publicOnly);

    /**
     * 获取指定用户的公开收藏列表（用于个人主页）
     */
    PageResult<PublicCollectionDTO> getPublicCollections(Long userId, int page, int size);
}
