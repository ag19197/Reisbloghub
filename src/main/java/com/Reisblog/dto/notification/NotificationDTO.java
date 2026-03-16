package com.Reisblog.dto.notification;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String type;
    private String content;
    private Long relatedId;
    private Boolean isRead;
    private LocalDateTime createTime;
}