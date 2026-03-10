package com.Reisblog.service;

import com.Reisblog.dto.PageResult;
import com.Reisblog.entity.SensitiveWord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SensitiveWordService extends IService<SensitiveWord> {
    /**
     * 分页查询屏蔽词列表
     * @param page 页码
     * @param size 每页条数
     * @param keyword 关键词（模糊匹配 word 字段）
     * @return 分页结果
     */
    PageResult<SensitiveWord> getPage(int page, int size, String keyword);
}
