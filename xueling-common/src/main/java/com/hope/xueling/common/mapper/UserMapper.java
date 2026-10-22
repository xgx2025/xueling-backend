package com.hope.xueling.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hope.xueling.common.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户ID更新用户账户状态为已注销(3-注销)
     * @param userId 用户ID
     */
    @Update("update user set account_status = 3 where id = #{userId}")
    void updateAccountStatusById(Long userId);

    /**
     * 判断用户是否为会员
     * @param userId 用户ID
     * @return true-会员，false-非会员
     */
    @Select("select id from user where id = #{userId} and vip_level > 0 " +
            "and vip_start_at <= CURRENT_TIMESTAMP and vip_expire_at > CURRENT_TIMESTAMP")
    Long isVip(Long userId);


}
