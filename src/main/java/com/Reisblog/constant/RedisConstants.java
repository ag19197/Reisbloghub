package com.Reisblog.constant;

public class RedisConstants {
    // 热门文章排行榜（使用Sorted Set）
    public static final String HOT_ARTICLE_ZSET = "hot:article";
    // 阅读数前缀（用于存储文章实时阅读数）
    public static final String ARTICLE_READ_COUNT = "article:read:count:";
    // 点赞数前缀
    public static final String ARTICLE_LIKE_COUNT = "article:like:count:";
    // 防刷相关（你已有的）
    public static final String ARTICLE_READ_IP = "article:read:ip:";
    public static final String ARTICLE_LIKE_IP = "article:like:ip:";
    // 可继续添加其他常量...
    public static final String HOT_ARTICLES_KEY = "hot:articles";

    public static final double LIKE_WEIGHT = 3.0;    // 点赞权重
    public static final double READ_WEIGHT = 1.0;    // 阅读权重
}