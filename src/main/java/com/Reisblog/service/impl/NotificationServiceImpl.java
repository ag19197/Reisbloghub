package com.Reisblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.notification.NotificationDTO;
import com.Reisblog.entity.Notification;
import com.Reisblog.exception.BusinessException;
import com.Reisblog.mapper.NotificationMapper;
import com.Reisblog.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public PageResult<NotificationDTO> getUserNotifications(Long userId, int page, int size) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime);
        Page<Notification> notificationPage = page(new Page<>(page, size), wrapper);
        List<NotificationDTO> dtoList = notificationPage.getRecords().stream()
                .map(noti -> BeanUtil.copyProperties(noti, NotificationDTO.class))
                .collect(Collectors.toList());
        return new PageResult<>(dtoList, notificationPage.getTotal(), size, page);
    }

    @Override
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = getById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }
        notification.setIsRead(true);
        updateById(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, false);
        Notification update = new Notification();
        update.setIsRead(true);
        update(update, wrapper);
    }
}