package com.hope.xueling.english.reading.service;

import com.hope.xueling.english.reading.domain.entity.Article;
import com.hope.xueling.english.reading.domain.entity.ArticleCategory;
import com.hope.xueling.english.reading.domain.vo.ArticleListVO;

import java.util.List;

/**
 * 阅读服务接口
 * @author 谢光益
 * @since 2026/1/26
 */
public interface IArticleService {

    /**
     * 获取阅读文章分类列表
     * @return 阅读文章分类列表
     */
    List<ArticleCategory> getArticleCategories();

    /**
     * 根据分类ID获取阅读材料列表
     * @param categoryId 分类ID
     * @return 阅读材料列表
     */
    List<ArticleListVO> getArticlesByCategoryId(Long categoryId);

    /**
     * 按分类查询用户可阅读的文章（含正文），用于阅读列表的完整数据
     * @param userId 用户 ID，用于会员与免费文章判定
     * @param categoryId 分类 ID
     * @return 可阅读文章列表
     */
    List<Article> getReadableArticlesByCategoryId(Long userId, Long categoryId);

    /**
     * 查询文章详情，并按用户会员状态校验可见性
     * @param userId 用户 ID
     * @param articleId 文章 ID
     * @return 文章详情
     */
    Article getArticleById(Long userId, Long articleId);

    /***
     * 获取文章翻译
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 文章翻译
     */
    String getArticleTranslation(Long userId, Long articleId);

    /**
     * 句子分析
     * @param userId 用户ID
     * @param sentence 句子
     * @return 句子分析结果
     */
    String sentenceAnalysis(Long userId, String sentence);

    /**
     * 词汇短语汇总
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 汇总结果
     */
    String summarizeEnglishPhrases(Long userId, Long articleId);

    /**
     * 阅读测试
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param difficulty 难度
     * @return 测试结果
     */
    String readTest(Long userId, Long articleId, Integer difficulty);

    /**
     * 重新阅读测试
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param difficulty 难度
     * @return 测试结果
     */
    String reReadTest(Long userId, Long articleId, Integer difficulty);

    /**
     * 文章收藏
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    void collectArticle(Long userId, Long articleId);

    /**
     * 更新阅读状态
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    void updateReadingStatus(Long userId, Long articleId);

     /**
     * 完成阅读文章
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    void completeReadArticle(Long userId, Long articleId);

    /**
     * 添加阅读时间
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param readTime 阅读时间
     */
    void addReadTime(Long userId, Long articleId, Integer readTime);

    /**
     * 取消文章收藏
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    void cancelCollectArticle(Long userId, Long articleId);
}
