package com.Reisblog.controller.front;

import com.Reisblog.dto.Result;
import com.Reisblog.entity.Tag;                // 实体类（只导入一次）
import com.Reisblog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    @Operation(summary = "获取所有标签")
    public Result<List<Tag>> getAllTags() {
        List<Tag> list = tagService.list();
        return Result.success(list);
    }
}