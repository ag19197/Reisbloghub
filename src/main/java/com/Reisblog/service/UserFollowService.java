package com.Reisblog.service;

import com.Reisblog.entity.UserFollow;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserFollowService extends IService<UserFollow> {
    boolean isFollowing(Long followerId, Long followeeId);
    void follow(Long followerId, Long followeeId);
    void unfollow(Long followerId, Long followeeId);
}