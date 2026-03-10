package com.Reisblog.controller.admin;

import com.Reisblog.dto.Result;
import com.Reisblog.entity.Category;
import com.Reisblog.service.CategoryService;
import com.Reisblog.utils.AdminAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Tag(name = "后台分类管理接口")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取所有分类")
    public Result<List<Category>> listAll(HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        List<Category> list = categoryService.list();
        return Result.success(list);
    }

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<Category> createCategory(@RequestBody Category category, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        categoryService.save(category);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改分类")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        category.setId(id);
        categoryService.updateById(category);
        return Result.success(category);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        categoryService.removeById(id);
        return Result.success();
    }
}