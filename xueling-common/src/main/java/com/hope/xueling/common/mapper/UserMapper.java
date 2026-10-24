package com.hope.xueling.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hope.xueling.common.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper：提供账号状态更新、会员有效性判断与账号状态查询
 * @author 谢光湘
 * @since 2026/10/24
 */
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

    /**
     * 查询用户账户状态(0-禁用, 1-正常, 3-注销)
     * @param userId 用户ID
     * @return 账户状态，用户不存在时返回 null
     */
    @Select("select account_status from user where id = #{userId}")
    Integer selectAccountStatus(Long userId);

}
