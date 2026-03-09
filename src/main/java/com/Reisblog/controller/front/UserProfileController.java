package com.Reisblog.controller.front;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.collection.PublicCollectionDTO;
import com.Reisblog.dto.user.UserProfileDTO;
import com.Reisblog.entity.User;
import com.Reisblog.service.CollectionService;
import com.Reisblog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户主页接口")
public class UserProfileController {

    private final UserService userService;
    private final CollectionService collectionService;

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
}