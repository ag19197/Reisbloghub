package com.Reisblog.dto.admin;

import lombok.Data;

import java.util.List;
import java.util.Map;

//后台数据统计
@Data
public class DashboardDTO {
    private Long articleCount;
    private Long commentCount;
    private Long userCount;
    private Long likeCount;
    private List<Map<String, Object>> readTrend;  // 每个元素包含 date 和 count
}
