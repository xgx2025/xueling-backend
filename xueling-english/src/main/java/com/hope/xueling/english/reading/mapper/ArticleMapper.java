package com.hope.xueling.english.reading.mapper;

import com.hope.xueling.english.reading.domain.entity.Article;
import com.hope.xueling.english.reading.domain.entity.ArticleCategory;
import com.hope.xueling.english.reading.domain.vo.ArticleListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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
    @Select("select id, name from article_category where is_active = 1 order by sort_order, id")
    List<ArticleCategory> selectArticleCategories();

    /**
     * 根据分类ID获取阅读文章文章列表
     *
     * @param categoryId 分类ID
     * @return 阅读文章文章列表
     */
    @Select("select id, title, chinese_title, category_id, tag, author, image_url, is_free, create_time " +
            "from article where category_id = #{categoryId} and publish_status = 'PUBLISHED' order by create_time desc, id desc")
    List<ArticleListVO> selectArticlesByCategoryId(Long categoryId);

    /**
     * 按分类查询用户可读的已发布文章（含正文）：免费文章对所有用户开放，非免费文章要求账号正常且会员在有效期内
     * @param categoryId 分类 ID
     * @param userId 用户 ID
     * @return 文章列表
     */
    @Select("select id, title, chinese_title, category_id, tag, author, content, chinese_meaning, " +
            "vocabulary_phrases_summary, article_insights, image_url, is_free, create_time, update_time " +
            "from article where category_id = #{categoryId} and publish_status = 'PUBLISHED' " +
            "and (is_free = 1 or exists (select 1 from user u where u.id = #{userId} " +
            "and u.account_status = 1 and u.vip_level > 0 " +
            "and u.vip_start_at <= CURRENT_TIMESTAMP and u.vip_expire_at > CURRENT_TIMESTAMP)) " +
            "order by create_time desc, id desc")
    List<Article> selectReadableArticlesByCategoryId(@Param("categoryId") Long categoryId,
                                                     @Param("userId") Long userId);

    /**
     * 查询用户可读的已发布文章详情，可见性规则同列表查询
     * @param articleId 文章 ID
     * @param userId 用户 ID
     * @return 文章详情，不可见时返回 null
     */
    @Select("select id, title, chinese_title, category_id, tag, author, content, chinese_meaning, " +
            "vocabulary_phrases_summary, article_insights, image_url, is_free, create_time, update_time " +
            "from article where id = #{articleId} and publish_status = 'PUBLISHED' " +
            "and (is_free = 1 or exists (select 1 from user u where u.id = #{userId} " +
            "and u.account_status = 1 and u.vip_level > 0 " +
            "and u.vip_start_at <= CURRENT_TIMESTAMP and u.vip_expire_at > CURRENT_TIMESTAMP))")
    Article selectPublishedArticleById(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 查询已发布文章的免费标记，用于可见性判断
     * @param articleId 文章 ID
     * @return 是否免费；文章不存在或未发布时返回 null
     */
    @Select("select is_free from article where id = #{articleId} and publish_status = 'PUBLISHED'")
    Boolean selectPublishedIsFree(Long articleId);

    /**
     * 根据文章ID获取文章翻译
     * @param userId    用户ID
     * @param articleId 文章ID
     * @return 文章翻译
     */
    @Select("select chinese_meaning from article where id = #{articleId} and publish_status = 'PUBLISHED' " +
            "and (is_free = 1 or exists (select 1 from user u where u.id = #{userId} " +
            "and u.account_status = 1 and u.vip_level > 0 " +
            "and u.vip_start_at <= CURRENT_TIMESTAMP and u.vip_expire_at > CURRENT_TIMESTAMP))")
    String selectArticleTranslation(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 根据文章ID获取文章词汇短语汇总结果
     *
     * @param articleId 文章ID
     * @return 文章词汇短语汇总结果
     */
    @Select("select vocabulary_phrases_summary from article where id = #{articleId} " +
            "and publish_status = 'PUBLISHED' " +
            "and (is_free = 1 or exists (select 1 from user u where u.id = #{userId} " +
            "and u.account_status = 1 and u.vip_level > 0 " +
            "and u.vip_start_at <= CURRENT_TIMESTAMP and u.vip_expire_at > CURRENT_TIMESTAMP))")
    String selectArticlePhrases(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 插入文章词汇短语汇总结果
     *
     * @param articleId               文章ID
     * @param summarizeEnglishPhrases 词汇短语汇总结果
     */
    @Update("update article set vocabulary_phrases_summary = #{summarizeEnglishPhrases} where id = #{articleId}")
    void updateArticlePhrases(Long articleId, String summarizeEnglishPhrases);

}
