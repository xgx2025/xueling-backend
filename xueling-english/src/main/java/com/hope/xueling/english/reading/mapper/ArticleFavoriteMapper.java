package com.hope.xueling.english.reading.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 文章收藏记录Mapper
 * @author 谢光益
 * @since 2026/1/31
 */
@Mapper
public interface ArticleFavoriteMapper {
    /**
     * 更新用户收藏文章记录
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    @Update("insert into article_favorite (user_id, article_id) values (#{userId}, #{articleId})")
    void updateCollectArticle(Long userId, Long articleId);

    /**
     * 删除用户收藏文章记录
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    @Update("delete from article_favorite where user_id = #{userId} and article_id = #{articleId}")
    void deleteCollectArticle(Long userId, Long articleId);
}
