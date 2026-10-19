package com.hope.xueling.common.domain.vo;

import lombok.Data;

/**
 * 用户视图对象
 * <p>对外输出的 id 统一使用 String 类型：雪花算法生成的 19 位 Long 超出
 * JavaScript 的安全整数范围（2^53-1），以数字形式返回会在前端丢精度。
 * 与 english 模块的 WordBookVO / WordVO 等保持同一约定。</p>
 * @author 谢光益
 * @since 2026/1/21
 */
@Data
public class UserVO {
    //用户ID（字符串形式，避免前端精度丢失）
    private String id;
    //用户名
    private String username;
    //邮箱
    private String email;
    //手机号
    private String phone;
    //头像URL
    private String avatarUrl;
    //性别
    private Integer gender;
    //生日
    private String birthday;
    //个人简介
    private String bio;
    //vip等级
    private Integer vipLevel;
    //vip开始时间
    private String vipStartTime;
    //vip到期时间
    private String vipExpireTime;
}
