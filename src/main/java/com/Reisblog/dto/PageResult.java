package com.Reisblog.dto;

import lombok.Data;

import java.util.List;

// 分页数据专用封装
@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int current;
    private int size;
    private int pages;

    public PageResult(List<T> records, long total, int current, int size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }

//    // 普通响应
//return Result.success(user);
//
//    // 分页响应
//    PageResult<User> page = new PageResult<>(list, total, pageNum, pageSize);
//return Result.success(page);
}
