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
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 收藏对象DTO
     */
    CollectionDTO addCollection(Long userId, Long articleId);

    /**
     * 取消收藏
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    void removeCollection(Long userId, Long articleId);

    /**
     * 修改收藏可见性
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param isPublic 是否公开
     */
    void updateVisibility(Long userId, Long articleId, Boolean isPublic);

    /**
     * 获取当前用户的收藏列表（可筛选公开/私密）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页条数
     * @param publicOnly true-只返回公开，false-返回全部，null-返回全部
     * @return 分页结果
     */
    PageResult<CollectionItemDTO> getUserCollections(Long userId, int page, int size, Boolean publicOnly);

    /**
     * 获取指定用户的公开收藏列表（用于个人主页）
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    PageResult<PublicCollectionDTO> getPublicCollections(Long userId, int page, int size);
}
