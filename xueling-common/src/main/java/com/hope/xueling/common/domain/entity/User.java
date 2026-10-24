package com.hope.xueling.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 * @author 谢光益
 * @since 2026/1/21
 */
@Data
@NoArgsConstructor
@TableName("user")
public class User {
    /**
     * 用户ID
     * <p>主键统一由 MyBatis-Plus 雪花算法生成（与 word_book 等表保持一致），
     * 不再依赖数据库的 AUTO_INCREMENT，也不在业务代码中手动赋值。</p>
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 密码
     */
    private String password;
    /**
     * 头像URL
     */
    private String avatarUrl;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 个人简介
     */
    private String bio;
    /**
     * vip等级
     */
    private Integer vipLevel;
    /**
     * vip开始时间
     */
    @TableField("vip_start_at")
    private String vipStartTime;
    /**
     * vip到期时间
     */
    @TableField("vip_expire_at")
    private String vipExpireTime;
}
