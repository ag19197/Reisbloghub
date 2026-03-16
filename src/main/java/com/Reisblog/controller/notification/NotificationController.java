package com.Reisblog.controller.notification;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.notification.NotificationDTO;
import com.Reisblog.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "消息通知接口")
public class NotificationController {

    private final NotificationService notificationService;

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping
    @Operation(summary = "获取当前用户的通知列表")
    public Result<PageResult<NotificationDTO>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.fail("未登录");
        }
        PageResult<NotificationDTO> result = notificationService.getUserNotifications(userId, page, size);
        return Result.success(result);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记单条通知为已读")
    public Result<Void> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.fail("未登录");
        }
        notificationService.markAsRead(userId, id);
        return Result.success();
    }

    @PutMapping("/read-all")
    @Operation(summary = "一键全部标记为已读")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.fail("未登录");
        }
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
}