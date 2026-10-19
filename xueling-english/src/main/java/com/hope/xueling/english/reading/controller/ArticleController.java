package com.hope.xueling.english.reading.controller;

import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.common.util.ThreadLocalUtils;
import com.hope.xueling.english.reading.domain.entity.Article;
import com.hope.xueling.english.reading.domain.entity.ArticleCategory;
import com.hope.xueling.english.reading.service.IArticleService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阅读控制器
 * @author 谢光益
 * @since 2026/1/26
 */
@Slf4j
@RequiredArgsConstructor
@RestController("userArticleController")
@RequestMapping("/read")
public class ArticleController {

    private final IArticleService readService;

    /**
     * 获取阅读文章分类列表
     * @return 阅读文章分类列表
     */
    @GetMapping("/categories")
    public Result<List<ArticleCategory>> getArticleCategories() {
        log.info("获取阅读文章分类列表");
        List<ArticleCategory> articleCategories = readService.getArticleCategories();
        return Result.success(articleCategories, "获取阅读文章分类列表成功");
    }

    /**
     * 根据分类ID获取阅读文章列表
     * @param categoryId 分类ID
     * @return 阅读文章列表
     */
    @GetMapping("/articles")
    public Result<List<Article>> getArticlesByCategoryId(@RequestParam("categoryId") Long categoryId) {
        log.info("根据分类ID获取阅读文章列表，分类ID：{}", categoryId);
        List<Article> articles = readService.getArticlesByCategoryId(categoryId);
        return Result.success(articles, "根据分类ID获取阅读文章列表成功");
    }

    /**
     * 根据文章ID获取文章翻译
     * @param articleId 文章ID
     * @return 文章翻译
     * 翻译文章内容格式：每个小标题前加##，段落之间用\n分隔，句子之间用&分隔
     */
    @GetMapping("/article/translation")
    public Result<String> getArticleTranslation(@RequestParam("articleId") Long articleId) {
        log.info("根据文章ID获取文章翻译，文章ID：{}", articleId);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        String articleTranslation = readService.getArticleTranslation(userId,articleId);
        return Result.success(articleTranslation, "根据文章ID获取文章翻译成功");
    }

    /**
     * 句子分析
     * @param sentence 句子
     * @return 句子分析结果
     */
    @GetMapping("/sentence")
    public Result<String> sentenceAnalysis(@RequestParam("sentence") String sentence) {
        log.info("句子分析，句子：{}", sentence);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        String sentenceAnalysis = readService.sentenceAnalysis(userId,sentence);
        return Result.success(sentenceAnalysis, "句子分析成功");
    }

    /**
     * 词汇短语汇总
     * @param articleId 文章ID
     * @return 汇总结果
     */
    @GetMapping("/phrases")
    public Result<String> summarizeEnglishPhrases(@RequestParam("articleId") Long articleId) {
        log.info("词汇短语汇总，文章ID：{}", articleId);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        String summarizeEnglishPhrases = readService.summarizeEnglishPhrases(userId,articleId);
        return Result.success(summarizeEnglishPhrases, "词汇短语汇总成功");
    }

    /**
     * 获取阅读测试题
     * @param articleId 文章ID
     * @param difficulty 难度
     * @return 测试题
     */
    @PostMapping("/test")
    public Result<String> readTest(@RequestParam("articleId") Long articleId,@RequestParam("difficulty") Integer difficulty) {
        log.info("阅读测试题，文章ID：{}，难度：{}", articleId,difficulty);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        String test = readService.readTest(userId,articleId,difficulty);
        return Result.success(test, "阅读测试题成功");
    }

    /**
     * 重新生成阅读测试题
     * @param articleId 文章ID
     * @param difficulty 难度
     * @return 测试题
     */
    @PostMapping("/retest")
    public Result<String> reReadTest(@RequestParam("articleId") Long articleId,@RequestParam("difficulty") Integer difficulty) {
        log.info("重新生成阅读测试题，文章ID：{}，难度：{}", articleId,difficulty);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        String test = readService.reReadTest(userId,articleId,difficulty);
        return Result.success(test, "重新生成阅读测试题成功");
    }

    /**
     * 增加文章阅读时间（秒）
     * @param articleId 文章ID
     * @param readTime 阅读时间（秒）（要大于等于150秒）
     */
    @PostMapping("/readTime")
    public Result<String> addReadTime(@RequestParam("articleId") Long articleId,@RequestParam("readTime") Integer readTime) {
        log.info("增加文章阅读时间，文章ID：{}，阅读时间：{}", articleId,readTime);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        readService.addReadTime(userId,articleId,readTime);
        return Result.success("增加文章阅读时间成功");
    }

    /**
     * 更新阅读进度为阅读中
     * @param articleId 文章ID
     */
    @PostMapping("/reading")
    public Result<String> updateReadingStatus(@RequestParam("articleId") Long articleId) {
        log.info("更新阅读进度为阅读中，文章ID：{}", articleId);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        readService.updateReadingStatus(userId,articleId);
        return Result.success("更新阅读进度为阅读中成功");
    }
    /**
     * 完成阅读文章（需要阅读时间超过2.5分钟）
     * @param articleId 文章ID
     * @return 完成结果
     */
    @PostMapping("/complete")
    public Result<String> completeReadArticle(@RequestParam("articleId") Long articleId) {
        log.info("完成文章阅读，文章ID：{}", articleId);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        readService.completeReadArticle(userId,articleId);
        return Result.success("完成文章阅读");
    }

    /**
     * 文章收藏
     * @param articleId 文章ID
     * @return 收藏结果
     */
    @PostMapping("/collect")
    public Result<String> collectArticle(@RequestParam("articleId") Long articleId) {
        log.info("文章收藏，文章ID：{}", articleId);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        readService.collectArticle(userId,articleId);
        return Result.success("文章收藏成功");
    }

    /**
     * 取消文章收藏
     * @param articleId 文章ID
     * @return 取消收藏结果
     */
    @DeleteMapping("/cancelCollect")
    public Result<String> cancelCollectArticle(@RequestParam("articleId") Long articleId) {
        log.info("取消文章收藏，文章ID：{}", articleId);
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        readService.cancelCollectArticle(userId,articleId);
        return Result.success("取消文章收藏成功");
    }

}
