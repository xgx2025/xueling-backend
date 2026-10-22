package com.hope.xueling.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hope.xueling.common.domain.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
    @Select("SELECT * FROM user_feedback WHERE user_id = #{userId} ORDER BY create_time DESC, id DESC LIMIT #{limit} OFFSET #{offset}")
    List<Feedback> listByUser(@Param("userId") long userId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("<script>SELECT * FROM user_feedback <where><if test='status != null'>status = #{status}</if></where> ORDER BY create_time DESC, id DESC LIMIT #{limit} OFFSET #{offset}</script>")
    List<Feedback> listForAdmin(@Param("status") String status, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM user_feedback WHERE user_id = #{userId} AND create_time >= DATE_SUB(NOW(), INTERVAL 1 HOUR)")
    int countRecent(@Param("userId") long userId);
}
