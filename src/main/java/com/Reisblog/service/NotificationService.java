package com.Reisblog.service;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.notification.NotificationDTO;
import com.Reisblog.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;

public interface NotificationService extends IService<Notification> {
    /**
     * 分页获取当前用户的通知列表
     */
    PageResult<NotificationDTO> getUserNotifications(Long userId, int page, int size);

    /**
     * 标记单条通知为已读
     */
    void markAsRead(Long userId, Long notificationId);

    /**
     * 一键全部标记为已读
     */
    void markAllAsRead(Long userId);
}