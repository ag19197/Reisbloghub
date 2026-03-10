package com.Reisblog.service.impl;

import com.Reisblog.entity.Category;
import com.Reisblog.mapper.CategoryMapper;
import com.Reisblog.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
