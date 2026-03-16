package com.Reisblog.controller.front;

import com.Reisblog.dto.Result;
import com.Reisblog.entity.Category;
import com.Reisblog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "公共分类接口")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取所有分类")
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.list());
    }
}