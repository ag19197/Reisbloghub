package com.Reisblog.controller.admin;

import com.Reisblog.dto.Result;
import com.Reisblog.dto.admin.DashboardDTO;
import com.Reisblog.service.ArticleService;
import com.Reisblog.service.CommentService;
import com.Reisblog.service.UserService;
import com.Reisblog.utils.AdminAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "后台仪表盘接口")
public class AdminDashboardController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "获取仪表盘统计数据")
    public Result<DashboardDTO> getDashboard(HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        DashboardDTO dto = new DashboardDTO();
        // 文章总数
        dto.setArticleCount(articleService.count());
        // 评论总数
        dto.setCommentCount(commentService.count());
        // 用户总数
        dto.setUserCount(userService.count());
        // 点赞总数（需要额外查询，这里先设为0）
        dto.setLikeCount(0L);
        // 近7日阅读趋势（模拟数据，实际需要从文章表中按日期统计）
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", 0); // 实际需查询
            trend.add(item);
        }
        dto.setReadTrend(trend);
        return Result.success(dto);
    }
}