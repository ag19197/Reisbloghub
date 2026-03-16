package com.Reisblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Reisblog.constant.RedisConstants;
import com.Reisblog.dto.PageResult;
import com.Reisblog.dto.article.AdminArticleDTO;
import com.Reisblog.dto.article.ArticleDetailDTO;
import com.Reisblog.dto.article.ArticleListItemDTO;
import com.Reisblog.dto.like.LikeResultDTO;
import com.Reisblog.entity.*;
import com.Reisblog.exception.BusinessException;
import com.Reisblog.mapper.ArticleMapper;
import com.Reisblog.mapper.ArticleTagMapper;
import com.Reisblog.mapper.CategoryMapper;
import com.Reisblog.mapper.TagMapper;
import com.Reisblog.service.ArticleService;
import com.Reisblog.service.NotificationService;
import com.Reisblog.service.UserService;
import com.Reisblog.utils.MarkdownUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.Reisblog.constant.RedisConstants.HOT_ARTICLES_KEY;

@Service
@RequiredArgsConstructor //会为 final 字段生成构造器
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final StringRedisTemplate redisTemplate;   // 添加 RedisTemplate 注入
    private final NotificationService notificationService;
    private final UserService userService;
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

        if (Boolean.TRUE.equals(isFirstRead)) {
            article.setReadCount(article.getReadCount() + 1);
            updateById(article);
            // 阅读数增加，排行榜增加 READ_WEIGHT
            redisTemplate.opsForZSet().incrementScore(
                    RedisConstants.HOT_ARTICLE_ZSET,
                    id.toString(),
                    RedisConstants.READ_WEIGHT
            );
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
        // 判断当前IP是否已点赞
        String likeKey = "article:like:" + id + ":" + ip + ":" + LocalDate.now().toString();
        Boolean liked = redisTemplate.hasKey(likeKey);
        dto.setHasLiked(Boolean.TRUE.equals(liked));

        return dto;
    }

    @Override
    public LikeResultDTO likeArticle(Long articleId, Long userId, String ip) {
        String likeKey = "article:like:" + articleId + ":" + ip + ":" + LocalDate.now().toString();
        Boolean isLiked = redisTemplate.hasKey(likeKey);

        Article article = getById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        if (Boolean.TRUE.equals(isLiked)) {
            // 取消点赞
            redisTemplate.delete(likeKey);
            article.setLikeCount(article.getLikeCount() - 1);
            redisTemplate.opsForZSet().incrementScore(
                    RedisConstants.HOT_ARTICLE_ZSET,
                    articleId.toString(),
                    -RedisConstants.LIKE_WEIGHT
            );
        } else {
            // 点赞
            redisTemplate.opsForValue().set(likeKey, "1", 1, TimeUnit.DAYS);
            article.setLikeCount(article.getLikeCount() + 1);
            redisTemplate.opsForZSet().incrementScore(
                    RedisConstants.HOT_ARTICLE_ZSET,
                    articleId.toString(),
                    RedisConstants.LIKE_WEIGHT
            );
            // 发送通知给文章作者（如果不是自己给自己点赞）
            if (article.getUserId() != null && !article.getUserId().equals(userId)) {
                User currentUser = userService.getById(userId);
                String nickname = currentUser != null ? currentUser.getNickname() : "某用户";
                Notification notification = new Notification();
                notification.setUserId(article.getUserId());
                notification.setType("LIKE");
                notification.setContent("用户 " + nickname + " 点赞了你的文章：《" + article.getTitle() + "》");
                notification.setRelatedId(articleId);
                notification.setIsRead(false);
                notificationService.save(notification);
            }
        }
        updateById(article);

        return new LikeResultDTO(article.getLikeCount(), isLiked ? "unlike" : "like");
    }

    @Override
    public PageResult<AdminArticleDTO> getAdminArticles(int page, int size, Integer status, Long categoryId, String keyword) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword);
        }
        wrapper.orderByDesc(Article::getCreateTime);
        Page<Article> articlePage = page(new Page<>(page, size), wrapper);
        List<AdminArticleDTO> dtoList = articlePage.getRecords().stream()
                .map(article -> BeanUtil.copyProperties(article, AdminArticleDTO.class))
                .collect(Collectors.toList());
        return new PageResult<>(dtoList, articlePage.getTotal(), size, page);
    }

    @Override
    @Transactional
    public Article saveArticle(Article article, List<Long> tagIds) {
        // 1. 校验必填字段
        if (article.getTitle() == null || article.getTitle().isEmpty()) {
            throw new BusinessException("文章标题不能为空");
        }
        if (article.getContent() == null || article.getContent().isEmpty()) {
            throw new BusinessException("文章内容不能为空");
        }
        // 2. 设置默认值
        if (article.getStatus() == null) {
            article.setStatus(0); // 默认为草稿
        }
        if (article.getAuthor() == null) {
            article.setAuthor("admin"); // 默认作者
        }
        article.setReadCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setIsTop(0);

        // 生成 HTML 内容
        if (article.getContent() != null) {
            // 如果引入 Markdown 解析器
             article.setContentHtml(MarkdownUtils.render(article.getContent()));
            // 临时方案：直接复制内容
//            article.setContentHtml(article.getContent());
        } else {
            article.setContentHtml("");
        }


        // 3. 保存文章
        this.save(article);

        // 4. 保存文章-标签关联
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }

        return article;
    }

    @Override
    @Transactional
    public Article updateArticle(Article article, List<Long> tagIds) {
        // 1. 检查文章是否存在
        Article existing = this.getById(article.getId());
        if (existing == null) {
            throw new BusinessException("文章不存在");
        }

        // 如果传入了新的 content，重新生成 contentHtml
        if (article.getContent() != null && !article.getContent().equals(existing.getContent())) {
            //article.setContentHtml(article.getContent()); // 或使用 MarkdownUtils.render
            article.setContentHtml(MarkdownUtils.render(article.getContent()));
        }

        // 2. 更新文章
        this.updateById(article);

        // 3. 更新标签：先删除原有关联，再插入新的
        if (tagIds != null) {
            // 先删除原有关联
            articleTagMapper.deleteByArticleId(article.getId());
            // 再插入新关联
            for (Long tagId : tagIds) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }

        return article;
    }

    @Override
    public void updateHotScore(Long articleId) {
        Article article = getById(articleId);
        if (article == null) return;
        // 计算得分：阅读数权重1，点赞数权重2（可调整）
        double score = article.getReadCount() * 1.0 + article.getLikeCount() * 2.0;
        redisTemplate.opsForZSet().add(HOT_ARTICLES_KEY, String.valueOf(articleId), score);
    }

    @Override
    public List<Article> getHotArticles(int top) {
        // 1. 尝试从 Redis 获取热门文章 ID
        Set<String> hotIds = redisTemplate.opsForZSet().reverseRange(RedisConstants.HOT_ARTICLE_ZSET, 0, top - 1);
        if (hotIds != null && !hotIds.isEmpty()) {
            // Redis 有数据，根据 ID 查询文章并保持顺序
            List<Long> idList = hotIds.stream().map(Long::valueOf).collect(Collectors.toList());
            List<Article> articles = listByIds(idList);
            Map<Long, Article> articleMap = articles.stream()
                    .collect(Collectors.toMap(Article::getId, a -> a));
            return idList.stream()
                    .map(articleMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // 2. Redis 无数据，从数据库获取所有已发布文章
        List<Article> allArticles = list(); // 获取所有文章（包括草稿）
        List<Article> published = allArticles.stream()
                .filter(a -> a.getStatus() != null && a.getStatus() == 1) // 只取已发布
                .collect(Collectors.toList());

        if (published.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 按热度计算分数并排序（点赞权重3，阅读权重1）
        published.sort((a, b) -> {
            double scoreA = a.getLikeCount() * RedisConstants.LIKE_WEIGHT
                    + a.getReadCount() * RedisConstants.READ_WEIGHT;
            double scoreB = b.getLikeCount() * RedisConstants.LIKE_WEIGHT
                    + b.getReadCount() * RedisConstants.READ_WEIGHT;
            return Double.compare(scoreB, scoreA);
        });

        // 4. 取前 top 篇
        List<Article> topArticles = published.stream().limit(top).collect(Collectors.toList());

        // 5. 将这批文章写入 Redis，方便下次快速获取
        for (Article article : topArticles) {
            double score = article.getLikeCount() * RedisConstants.LIKE_WEIGHT
                    + article.getReadCount() * RedisConstants.READ_WEIGHT;
            redisTemplate.opsForZSet().add(RedisConstants.HOT_ARTICLE_ZSET,
                    article.getId().toString(), score);
        }

        System.out.println("尝试从 Redis 获取热门文章，结果: " + hotIds);
        if (hotIds == null || hotIds.isEmpty()) {
            System.out.println("Redis 无数据，将从数据库加载");
        }

        return topArticles;
    }

    @Override
    public void initHotRanking() {
        // 查询所有文章（可根据需要只查询已发布的文章）
        List<Article> articles = list();
        if (articles.isEmpty()) {
            return;
        }
        // 清除旧的排行榜数据（可选）
        redisTemplate.delete(RedisConstants.HOT_ARTICLE_ZSET);
        // 批量添加分数
        for (Article article : articles) {
            double score = article.getLikeCount() * RedisConstants.LIKE_WEIGHT
                    + article.getReadCount() * RedisConstants.READ_WEIGHT;
            redisTemplate.opsForZSet().add(
                    RedisConstants.HOT_ARTICLE_ZSET,
                    article.getId().toString(),
                    score
            );
        }
    }

    @Override
    public PageResult<ArticleListItemDTO> getUserArticles(Long userId, int page, int size) {
        // 1. 根据用户ID获取用户信息，得到昵称
        User user = userService.getById(userId);  // 需要注入 UserService
        if (user == null) {
            return new PageResult<>(new ArrayList<>(), 0, size, page);
        }

        // 2. 查询该用户发布的文章（按作者昵称）
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getAuthor, user.getNickname()) // 假设用作者昵称关联
                .orderByDesc(Article::getCreateTime);
        Page<Article> articlePage = page(new Page<>(page, size), wrapper);

        // 3. 转换为 DTO（可复用已有转换逻辑，建议提取公共方法）
        List<ArticleListItemDTO> dtoList = articlePage.getRecords().stream()
                .map(this::convertToListItemDTO) // 假设你有一个转换方法
                .collect(Collectors.toList());

        return new PageResult<>(dtoList, articlePage.getTotal(), size, page);
    }

    // 可以提取一个私有方法，从 Article 转换为 ArticleListItemDTO
    private ArticleListItemDTO convertToListItemDTO(Article article) {
        ArticleListItemDTO dto = BeanUtil.copyProperties(article, ArticleListItemDTO.class);
        // 设置分类名称（需要 CategoryMapper）
        Category category = categoryMapper.selectById(article.getCategoryId());
        dto.setCategoryName(category != null ? category.getName() : null);
        // 设置标签列表（需要 ArticleTagMapper）
        List<String> tagNames = articleTagMapper.selectTagNamesByArticleId(article.getId());
        dto.setTags(tagNames);
        return dto;
    }

    // 辅助方法：查询文章的标签名列表
    private List<String> getTagNamesByArticleId(Long articleId) {
        // 直接调用自定义方法，无需再查 ArticleTag 实体
        return articleTagMapper.selectTagNamesByArticleId(articleId);
    }

}
