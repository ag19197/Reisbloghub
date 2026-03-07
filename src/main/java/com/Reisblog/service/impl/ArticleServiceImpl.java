package com.Reisblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.article.ArticleListItemDTO;
import com.Reisblog.entity.Article;
import com.Reisblog.entity.Category;
import com.Reisblog.mapper.ArticleMapper;
import com.Reisblog.mapper.CategoryMapper;
import com.Reisblog.mapper.TagMapper;
import com.Reisblog.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    // 注意：如果需要联查文章标签，通常需要自定义Mapper SQL，这里为了简单，先略过标签查询

    @Override
    public PageResult<ArticleListItemDTO> getArticleList(int page, int size, Long categoryId, Long tagId, String keyword) {
        // 1. 构建查询条件（只查已发布）
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword);
        }
        wrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getCreateTime);

        // 2. 分页查询文章
        Page<Article> articlePage = page(new Page<>(page, size), wrapper);
        List<Article> articles = articlePage.getRecords();

        if (articles.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0, size, page);
        }

        // 3. 收集所有分类ID
        Set<Long> categoryIds = articles.stream()
                .map(Article::getCategoryId)
                .collect(Collectors.toSet());

        // 4. 批量查询分类信息
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().in(Category::getId, categoryIds)
        );
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 5. 转换为 DTO，并设置分类名称（标签暂时留空）
        List<ArticleListItemDTO> dtoList = articles.stream().map(article -> {
            ArticleListItemDTO dto = BeanUtil.copyProperties(article, ArticleListItemDTO.class);
            dto.setCategoryName(categoryNameMap.get(article.getCategoryId()));
            // 标签列表暂不处理
            dto.setTags(new ArrayList<>());
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(dtoList, articlePage.getTotal(), size, page);
    }
}
