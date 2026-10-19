package com.hope.xueling.english.reading.mapper;

import com.hope.xueling.english.reading.domain.entity.Article;
import com.hope.xueling.english.reading.domain.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 阅读材料Mapper
 * @author 谢光益
 * @since 2026/1/26
 */
@Mapper
public interface ArticleMapper {
    /**
     * 获取阅读材料分类列表
     * @return 阅读文章分类列表
     */
    @Select("select distinct id, name from article_category where is_active = 1 order by sort_order")
    List<ArticleCategory> selectArticleCategories();

    /**
     * 根据分类ID获取阅读文章文章列表
     *
     * @param categoryId 分类ID
     * @return 阅读文章文章列表
     */
    @Select("select id, title, author, content from article where category_id = #{categoryId}")
    List<Article> selectArticlesByCategoryId(Long categoryId);

    /**
     * 根据文章ID获取文章翻译
     * @param userId    用户ID
     * @param articleId 文章ID
     * @return 文章翻译
     */
    String selectArticleTranslation(Long userId, Long articleId);

    /**
     * 根据文章ID获取文章词汇短语汇总结果
     *
     * @param articleId 文章ID
     * @return 文章词汇短语汇总结果
     */
    @Select("select id, vocabulary_phrases_summary from article where id = #{articleId}")
    Map<Long, String> selectArticlePhrases(Long articleId);

    /**
     * 根据文章ID获取文章内容
     *
     * @param articleId 文章ID
     * @return 文章内容
     */
    @Select("select content from article where id = #{articleId}")
    String selectContentById(Long articleId);

    /**
     * 插入文章词汇短语汇总结果
     *
     * @param articleId               文章ID
     * @param summarizeEnglishPhrases 词汇短语汇总结果
     */
    @Update("update article set vocabulary_phrases_summary = #{summarizeEnglishPhrases} where id = #{articleId}")
    void updateArticlePhrases(Long articleId, String summarizeEnglishPhrases);

    /**
     * 根据文章ID获取文章标题
     * @param articleId 文章ID
     * @return 文章标题
     */
    @Select("select title from article where id = #{articleId}")
    String selectTitleById(Long articleId);
}
