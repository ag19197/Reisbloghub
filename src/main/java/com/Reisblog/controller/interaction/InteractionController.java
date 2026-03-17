package com.Reisblog.controller.interaction;

import com.Reisblog.dto.Result;
import com.Reisblog.service.UserFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/interactions")
@RequiredArgsConstructor
@Tag(name = "用户互动接口")
public class InteractionController {

    private final UserFollowService userFollowService;

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @PostMapping("/follow/{userId}")
    @Operation(summary = "关注用户")
    public Result<Void> follow(@PathVariable Long userId, HttpServletRequest request) {
        Long followerId = getCurrentUserId(request);
        if (followerId == null) {
            return Result.fail("请先登录");
        }
        userFollowService.follow(followerId, userId);
        return Result.success();
    }

    @DeleteMapping("/follow/{userId}")
    @Operation(summary = "取消关注")
    public Result<Void> unfollow(@PathVariable Long userId, HttpServletRequest request) {
        Long followerId = getCurrentUserId(request);
        if (followerId == null) {
            return Result.fail("请先登录");
        }
        userFollowService.unfollow(followerId, userId);
        return Result.success();
    }

    @GetMapping("/follow/{userId}/status")
    @Operation(summary = "查询当前登录用户是否关注了指定用户")
    public Result<Boolean> checkFollowStatus(@PathVariable Long userId, HttpServletRequest request) {
        Long followerId = getCurrentUserId(request);
        if (followerId == null) {
            return Result.success(false); // 未登录相当于未关注
        }
        boolean isFollowing = userFollowService.isFollowing(followerId, userId);
        return Result.success(isFollowing);
    }
}