package com.Reisblog.service.impl;

import com.Reisblog.dto.PageResult;
import com.Reisblog.entity.SensitiveWord;
import com.Reisblog.mapper.SensitiveWordMapper;
import com.Reisblog.service.SensitiveWordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {
    @Override
    public PageResult<SensitiveWord> getPage(int page, int size, String keyword) {
        // 1. 构建查询条件
        LambdaQueryWrapper<SensitiveWord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SensitiveWord::getWord, keyword);
        }
        wrapper.orderByDesc(SensitiveWord::getCreateTime);

        // 2. 执行分页查询
        Page<SensitiveWord> wordPage = this.page(new Page<>(page, size), wrapper);

        // 3. 封装为 PageResult 返回
        return new PageResult<>(wordPage.getRecords(), wordPage.getTotal(), size, page);
    }
}
