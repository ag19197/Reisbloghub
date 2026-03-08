package com.Reisblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.article.ArticleDetailDTO;
import com.Reisblog.dto.article.ArticleListItemDTO;
import com.Reisblog.dto.like.LikeResultDTO;
import com.Reisblog.entity.Article;
import com.Reisblog.entity.Category;
import com.Reisblog.mapper.ArticleMapper;
import com.Reisblog.mapper.ArticleTagMapper;
import com.Reisblog.mapper.CategoryMapper;
import com.Reisblog.mapper.TagMapper;
import com.Reisblog.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //会为 final 字段生成构造器
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final StringRedisTemplate redisTemplate;   // 添加 RedisTemplate 注入
    // TODO 注意：如果需要联查文章标签，通常需要自定义Mapper SQL，这里为了简单，先略过标签查询

    // 文章列表
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

    // 文章详情
    @Override
    public ArticleDetailDTO getArticleDetail(Long id, String ip) {
        // 1. 查询文章
        Article article = getById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 2. 阅读数防刷（Redis）
        String redisKey = "article:read:" + id + ":" + ip + ":" + LocalDate.now().toString();
        Boolean isFirstRead = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 1, TimeUnit.DAYS);
        if (Boolean.TRUE.equals(isFirstRead)) {
            // 当天首次访问，阅读数+1
            article.setReadCount(article.getReadCount() + 1);
            updateById(article); // 同步到数据库（或使用Redis异步累加）
            // 可选：使用Redis incr 异步累加，定时同步到DB
        }

        // 3. 转换为 DTO
        ArticleDetailDTO dto = BeanUtil.copyProperties(article, ArticleDetailDTO.class);

        // 4. 设置分类名称
        Category category = categoryMapper.selectById(article.getCategoryId());
        dto.setCategoryName(category != null ? category.getName() : null);

        // 5. 设置标签列表（需要联查）
        List<String> tagNames = getTagNamesByArticleId(id);
        dto.setTags(tagNames);

        // 6. 设置是否已点赞（基于IP，预留）
        // 待实现点赞功能后补充
        dto.setHasLiked(false);

        return dto;
    }

    @Override
        public LikeResultDTO likeArticle(Long articleId, String ip) {
        String likeKey = "article:like:" + articleId + ":" + ip + ":" + LocalDate.now().toString();

        // 检查是否已点赞
        Boolean isLiked = redisTemplate.hasKey(likeKey);

        if (Boolean.TRUE.equals(isLiked)) {
            // 已点赞，执行取消操作
            redisTemplate.delete(likeKey);
            // 文章点赞数 -1（使用 Redis 原子减，或直接操作数据库）
            Article article = getById(articleId);
            article.setLikeCount(article.getLikeCount() - 1);
            updateById(article);
            return new LikeResultDTO(article.getLikeCount(), "unlike");
        } else {
            // 未点赞，执行点赞操作
            redisTemplate.opsForValue().set(likeKey, "1", 1, TimeUnit.DAYS);
            Article article = getById(articleId);
            article.setLikeCount(article.getLikeCount() + 1);
            updateById(article);
            return new LikeResultDTO(article.getLikeCount(), "like");
        }
    }

    // 辅助方法：查询文章的标签名列表
    private List<String> getTagNamesByArticleId(Long articleId) {
        // 直接调用自定义方法，无需再查 ArticleTag 实体
        return articleTagMapper.selectTagNamesByArticleId(articleId);
    }

}
