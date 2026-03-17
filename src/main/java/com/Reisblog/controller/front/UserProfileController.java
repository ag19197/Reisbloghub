package com.Reisblog.controller.front;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.collection.PublicCollectionDTO;
import com.Reisblog.dto.user.UserProfileDTO;
import com.Reisblog.dto.user.UserPublicProfileDTO;
import com.Reisblog.entity.Article;
import com.Reisblog.entity.User;
import com.Reisblog.entity.UserFollow;
import com.Reisblog.service.ArticleService;
import com.Reisblog.service.CollectionService;
import com.Reisblog.service.UserFollowService;
import com.Reisblog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户主页接口")
public class UserProfileController {

    private final UserService userService;
    private final CollectionService collectionService;
    private final UserFollowService userFollowService;
    private final ArticleService articleService;

    @GetMapping("/profile/{userId}")
    @Operation(summary = "获取用户个人主页信息")
    public Result<UserProfileDTO> getUserProfile(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        // 1. 获取用户基本信息
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // 2. 获取该用户的公开收藏列表
        PageResult<PublicCollectionDTO> publicCollections = collectionService.getPublicCollections(userId, page, size);

        // 3. 组装 DTO
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUserId(user.getId());
        profile.setNickname(user.getNickname());
        profile.setAvatar(user.getAvatar());
        profile.setPublicCollections(publicCollections);

        return Result.success(profile);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取作者公开主页信息")
    public Result<UserPublicProfileDTO> getUserPublicProfile(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        UserPublicProfileDTO dto = new UserPublicProfileDTO();
        dto.setUserId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        // 统计粉丝数、关注数、文章数（需要注入 UserFollowService 和 ArticleService）
        long followerCount = userFollowService.count(new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getFolloweeId, userId));
        long followingCount = userFollowService.count(new LambdaQueryWrapper<UserFollow>().eq(UserFollow::getFollowerId, userId));
        long articleCount = articleService.count(new LambdaQueryWrapper<Article>().eq(Article::getUserId, userId).eq(Article::getStatus, 1));
        dto.setFollowerCount((int) followerCount);
        dto.setFollowingCount((int) followingCount);
        dto.setArticleCount((int) articleCount);
        return Result.success(dto);
    }

}