package com.hope.xueling.english.reading.service.impl;

import com.hope.xueling.common.ai.SmartReadingAssistant;
import com.hope.xueling.common.exception.ValidationException;
import com.hope.xueling.common.exception.ForbiddenException;
import com.hope.xueling.common.mapper.UserMapper;
import com.hope.xueling.english.reading.domain.entity.Article;
import com.hope.xueling.english.reading.domain.entity.ArticleCategory;
import com.hope.xueling.english.reading.domain.vo.ArticleListVO;
import com.hope.xueling.english.reading.mapper.ArticleFavoriteMapper;
import com.hope.xueling.english.reading.mapper.ArticleMapper;
import com.hope.xueling.english.reading.mapper.ReadingProgressMapper;
import com.hope.xueling.english.reading.mapper.ArticleReadingTestMapper;
import com.hope.xueling.english.reading.service.IArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 阅读服务实现类
 * @author 谢光益
 * @since 2026/1/26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements IArticleService {

    /**
     * 文章与分类的数据访问
     */
    private final ArticleMapper articleMapper;
    /**
     * 用户数据访问，用于会员判定
     */
    private final UserMapper userMapper;
    /**
     * 阅读测试题的数据访问
     */
    private final ArticleReadingTestMapper articleReadingTestMapper;
    /**
     * 阅读进度与阅读时长的数据访问
     */
    private final ReadingProgressMapper readingProgressMapper;
    /**
     * 文章收藏的数据访问
     */
    private final ArticleFavoriteMapper articleFavoriteMapper;
    /**
     * 大模型阅读助手，负责句子分析、词汇汇总与测试题生成
     */
    private final SmartReadingAssistant smartReadingAssistant;

    /**
     * 查询启用中的文章分类，按排序值升序
     * @return 分类列表
     */
    @Override
    public List<ArticleCategory> getArticleCategories() {
        return articleMapper.selectArticleCategories();
    }

    /**
     * 按分类查询已发布文章摘要，不加载正文
     * @param categoryId 分类 ID
     * @return 文章摘要列表
     */
    @Override
    public List<ArticleListVO> getArticlesByCategoryId(Long categoryId) {
        //检查分类ID是否为空
        if (categoryId == null) {
            throw new ValidationException("分类 ID 不能为空");
        }
        List<ArticleListVO> articles = articleMapper.selectArticlesByCategoryId(categoryId);
        return articles;
    }

    /**
     * 按分类查询用户可读的文章（含正文），免费与会员可见规则由 SQL 处理
     * @param userId 用户 ID
     * @param categoryId 分类 ID
     * @return 文章列表
     */
    @Override
    public List<Article> getReadableArticlesByCategoryId(Long userId, Long categoryId) {
        if (userId == null || categoryId == null) {
            throw new ValidationException("用户ID和分类ID不能为空");
        }
        return articleMapper.selectReadableArticlesByCategoryId(categoryId, userId);
    }

    /**
     * 查询已发布文章详情，并按免费标记与会员状态校验可见性
     * @param userId 用户 ID
     * @param articleId 文章 ID
     * @return 文章详情
     */
    @Override
    public Article getArticleById(Long userId, Long articleId) {
        /**
         * 校验文章可见性：文章必须已发布，非免费文章要求用户具备有效会员
         * @param userId 用户 ID
         * @param articleId 文章 ID
         */
        requireReadable(userId, articleId);
        Article article = articleMapper.selectPublishedArticleById(articleId, userId);
        if (article == null) throw new ValidationException("文章不存在或暂未发布");
        return article;
    }

    /**
     * 查询文章中文翻译，仅对可见用户开放，翻译缺失时报错
     * @param userId 用户 ID
     * @param articleId 文章 ID
     * @return 中文翻译
     */
    @Override
    public String getArticleTranslation(Long userId, Long articleId) {
        if (userId == null || articleId == null) {
            throw new ValidationException("用户ID和文章ID不能为空");
        }
        requireReadable(userId, articleId);
        String articleTranslation = articleMapper.selectArticleTranslation(articleId, userId);
        if (articleTranslation == null) {
            throw new ValidationException("文章翻译不存在");
        }
        return articleTranslation;
    }

    /**
     * 句子分析（会员功能）：直接调用大模型解析句子结构，结果不落库
     * @param userId 用户 ID
     * @param sentence 待分析的英文句子
     * @return 分析结果
     */
    @Override
    public String sentenceAnalysis(Long userId, String sentence) {
        if (userId == null || sentence == null || sentence.isEmpty()) {
            throw new ValidationException("用户ID和句子不能为空");
        }
        //检查用户是否是会员
        if (userMapper.isVip(userId) == null) {
            throw new ValidationException("用户不是会员，不能使用句子分析功能");
        }
        String analysisResult = smartReadingAssistant.analyzeEnglishSentence(sentence);
        if (analysisResult == null) {
            throw new ValidationException("句子分析结果不存在");
        }
        return analysisResult;
    }

    /**
     * 词汇短语汇总（会员功能）：优先读库中已有结果，缺失时调用大模型生成并回写
     * @param userId 用户 ID
     * @param articleId 文章 ID
     * @return 词汇短语汇总文本
     */
    @Override
    public String summarizeEnglishPhrases(Long userId, Long articleId) {
        if (userId == null || articleId == null) {
            throw new ValidationException("用户ID和文章ID不能为空");
        }
        requireReadable(userId, articleId);
        //检查用户是否是会员,并从数据库获取词汇短语汇总结果
        if (userMapper.isVip(userId) == null) {
            throw new ValidationException("用户不是会员，不能使用词汇短语汇总功能");
        }
        //先从数据库获取词汇短语汇总结果
        String articlePhrases = articleMapper.selectArticlePhrases(articleId, userId);
        if (articlePhrases == null || articlePhrases.isBlank()) {
            //获取文章内容
            String content = getArticleContentForAi(userId, articleId);
            //调用大模型进行词汇短语汇总
            String summarizeEnglishPhrases = smartReadingAssistant.summarizeEnglishPhrases(content);
            //更新数据库中的词汇短语汇总结果
            articleMapper.updateArticlePhrases(articleId, summarizeEnglishPhrases);
            return summarizeEnglishPhrases;
        }
        //返回词汇短语汇总结果
        return articlePhrases;
    }

    /**
     * 阅读测试（会员功能）：优先读库中该难度的测试题，缺失时生成并按是否存在记录选择插入或更新
     * @param userId 用户 ID
     * @param articleId 文章 ID
     * @param difficulty 难度级别
     * @return 测试题内容
     */
    @Override
    public String readTest(Long userId, Long articleId, Integer difficulty) {
        if (userId == null || articleId == null || difficulty == null || difficulty < 0) {
            throw new ValidationException("用户ID、文章ID和难度不能为空");
        }
        if (userMapper.isVip(userId) == null) {
            throw new ValidationException("用户不是会员，不能使用阅读测试功能");
        }
        requireReadable(userId, articleId);
        //先从数据库获取阅读测试题
        String readTest = articleReadingTestMapper.selectReadTest(userId, articleId, difficulty);

        if (readTest == null) {
            //获取文章内容
            String content = getArticleContentForAi(userId, articleId);
            //调用大模型生成测试题
            content = smartReadingAssistant.generateReadingTest(content + " 测试难度：" + difficulty);
            //如果测试题不存在，生成测试题
            if(articleReadingTestMapper.selectIdByArticleIdAndUserId(articleId, userId) == null) {
                articleReadingTestMapper.insertReadTest(userId, articleId, content, difficulty);
            } else {
                //如果测试id存在，更新数据库中的测试题
                articleReadingTestMapper.updateReadTest(userId, articleId, content, difficulty);
            }
            readTest = content;
        }
        //返回测试题内容
        return readTest;
    }

    /**
     * 重新生成阅读测试题（会员功能），生成后覆盖库中该难度的记录
     * @param userId 用户 ID
     * @param articleId 文章 ID
     * @param difficulty 难度级别
     * @return 新的测试题内容
     */
    @Override
    public String reReadTest(Long userId, Long articleId, Integer difficulty) {
        if (userId == null || articleId == null || difficulty == null || difficulty < 0) {
            throw new ValidationException("用户ID、文章ID和难度不能为空");
        }
        if (userMapper.isVip(userId) == null) {
            throw new ValidationException("用户不是会员，不能使用重新阅读测试功能");
        }
        requireReadable(userId, articleId);
        //获取文章内容
        String content = getArticleContentForAi(userId, articleId);
        //调用大模型生成测试题
        String reReadTest = smartReadingAssistant.generateReadingTest(content + " 测试难度：" + difficulty);
        //更新数据库中的测试题
        articleReadingTestMapper.updateReadTest(userId, articleId, reReadTest, difficulty);
        return reReadTest;
    }

    /**
     * 收藏文章，要求用户已完成该文章阅读
     * @param userId 用户 ID
     * @param articleId 文章 ID
     */
    @Override
    public void collectArticle(Long userId, Long articleId) {
        if (userId == null || articleId == null) {
            throw new ValidationException("用户ID和文章ID不能为空");
        }
        requireReadable(userId, articleId);
        //检查用户是否阅读完文章
        if (readingProgressMapper.selectReadStatus(userId, articleId) == null) {
            throw new ValidationException("用户未阅读完文章，不能收藏文章");
        }

        //更新数据库中的文章收藏
        articleFavoriteMapper.updateCollectArticle(userId, articleId);
    }

    /**
     * 将阅读进度标记为阅读中
     * @param userId 用户 ID
     * @param articleId 文章 ID
     */
    @Override
    public void updateReadingStatus(Long userId, Long articleId) {
        if(userId == null || articleId == null) {
            throw new ValidationException("用户ID和文章ID不能为空");
        }
        requireReadable(userId, articleId);
        //更新数据库中的阅读进度为阅读中
        readingProgressMapper.updateReadingStatus(userId, articleId);
    }

    /**
     * 完成阅读，要求累计阅读时长不少于 150 秒
     * @param userId 用户 ID
     * @param articleId 文章 ID
     */
    @Override
    public void completeReadArticle(Long userId, Long articleId) {
        if(userId == null || articleId == null) {
            throw new ValidationException("用户ID和文章ID不能为空");
        }
        requireReadable(userId, articleId);
        //检查用户是否阅读时间是否超过2.5分钟(150秒)
        if (readingProgressMapper.selectReadTime(userId, articleId) < 150) {
            throw new ValidationException("总阅读时间不足2.5分钟，不能完成阅读");
        }
        //更新数据库中的阅读进度
        readingProgressMapper.updateReadStatus(userId, articleId);
    }

    /**
     * 累加文章阅读时长，单次上报需不少于 150 秒
     * @param userId 用户 ID
     * @param articleId 文章 ID
     * @param readTime 本次阅读时长（秒）
     */
    @Override
    public void addReadTime(Long userId, Long articleId, Integer readTime) {
        //更新数据库中的阅读时间
        if(userId == null || articleId == null || readTime == null || readTime < 0) {
            throw new ValidationException("用户ID、文章ID和阅读时间不能为空");
        }
        requireReadable(userId, articleId);
        //时间要大于等于150秒
        if (readTime < 150) {
            throw new ValidationException("阅读时间必须大于等于150秒");
        }
        //更新数据库中的阅读时间
        readingProgressMapper.updateReadTime(userId, articleId, readTime);
    }

    /**
     * 取消文章收藏
     * @param userId 用户 ID
     * @param articleId 文章 ID
     */
    @Override
    public void cancelCollectArticle(Long userId, Long articleId) {
        if(userId == null || articleId == null) {
            throw new ValidationException("用户ID和文章ID不能为空");
        }
        requireReadable(userId, articleId);
        //更新数据库中的文章收藏
        articleFavoriteMapper.deleteCollectArticle(userId, articleId);
    }

    /**
     * 获取阅读文章内容
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 文章内容
     */
    private String getArticleContentForAi(Long userId, Long articleId) {
        Article article = getArticleById(userId, articleId);
        String content = article.getContent();
        if (content == null) {
            throw new ValidationException("文章内容不存在");
        }
        return "文章的格式：每个小标题前有##，段落之间用\\n分隔，句子之间用&分隔\n文章标题：" + article.getTitle() + "\n文章的内容；\n" + content;
    }

    private void requireReadable(Long userId, Long articleId) {
        if (userId == null || articleId == null) {
            throw new ValidationException("用户ID和文章ID不能为空");
        }
        Boolean isFree = articleMapper.selectPublishedIsFree(articleId);
        if (isFree == null) {
            throw new ValidationException("文章不存在或暂未发布");
        }
        if (!isFree && userMapper.isVip(userId) == null) {
            throw new ForbiddenException("会员文章需开通有效会员");
        }
    }
}
