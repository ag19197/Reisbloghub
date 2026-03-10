package com.Reisblog.controller.admin;

import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.Result;
import com.Reisblog.entity.SensitiveWord;
import com.Reisblog.service.SensitiveWordService;
import com.Reisblog.utils.AdminAuthUtil;
import com.Reisblog.utils.SensitiveWordUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/sensitive-words")
@RequiredArgsConstructor
@Tag(name = "后台屏蔽词管理接口")
public class AdminSensitiveWordController {

    private final SensitiveWordService sensitiveWordService;

    @GetMapping
    @Operation(summary = "分页获取屏蔽词列表")
    public Result<PageResult<SensitiveWord>> listWords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        // 可以简单实现分页查询
        PageResult<SensitiveWord> result = sensitiveWordService.getPage(page, size, keyword);
        return Result.success(result);
    }

    @PostMapping
    @Operation(summary = "新增屏蔽词")
    public Result<SensitiveWord> addWord(@RequestBody SensitiveWord word, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        sensitiveWordService.save(word);
        // 刷新工具类缓存
        refreshCache();
        return Result.success(word);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改屏蔽词")
    public Result<SensitiveWord> updateWord(@PathVariable Long id, @RequestBody SensitiveWord word, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        word.setId(id);
        sensitiveWordService.updateById(word);
        refreshCache();
        return Result.success(word);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除屏蔽词")
    public Result<Void> deleteWord(@PathVariable Long id, HttpServletRequest request) {
        AdminAuthUtil.getAdminId(request);
        sensitiveWordService.removeById(id);
        refreshCache();
        return Result.success();
    }

    private void refreshCache() {
        List<SensitiveWord> words = sensitiveWordService.list();
        List<String> wordList = words.stream().map(SensitiveWord::getWord).collect(Collectors.toList());
        SensitiveWordUtils.refresh(wordList);
    }
}
