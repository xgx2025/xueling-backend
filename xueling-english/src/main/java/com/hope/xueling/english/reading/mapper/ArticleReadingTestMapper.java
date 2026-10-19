package com.hope.xueling.english.reading.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Mapper;

/**
 * 阅读测试题Mapper
 * @author 谢光益
 * @since 2026/1/30
 */
@Mapper
public interface ArticleReadingTestMapper {

    /**
     * 更新阅读测试题
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param readTest 阅读测试题
     */
    @Update("update article_reading_test set content = #{readTest}, difficulty = #{difficulty} where user_id = #{userId} and article_id = #{articleId}")
    void updateReadTest(Long userId, Long articleId, String readTest, Integer difficulty);

    /**
     * 查询阅读测试题
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 阅读测试题
     */
    @Select("select id, content from article_reading_test where user_id = #{userId} and article_id = #{articleId} and difficulty = #{difficulty}")
    String selectReadTest(Long userId, Long articleId, Integer difficulty);

    /**
     * 插入阅读测试题
     * @param userId 用户ID
     * @param articleId 文章ID
     * @param content 测试题
     */
    @Update("insert into article_reading_test (user_id, article_id, content, difficulty) values (#{userId}, #{articleId}, #{content}, #{difficulty})")
    void insertReadTest(Long userId, Long articleId, String content, Integer difficulty);

    /**
     * 查询文章ID和用户ID是否存在
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否存在
     */
    @Select("select id from article_reading_test where article_id = #{articleId} and user_id = #{userId}")
    Long selectIdByArticleIdAndUserId(Long articleId, Long userId);
}
