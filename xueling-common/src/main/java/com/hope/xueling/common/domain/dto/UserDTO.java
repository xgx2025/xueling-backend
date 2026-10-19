package com.hope.xueling.common.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户数据传输对象
 * <p>id 使用 String 接收，与 UserVO 的对外约定保持一致；
 * 服务端会自行转换为 Long 后再参与数据库操作。</p>
 * @author 谢光益
 * @since 2026/1/21
 */
@Data
@NoArgsConstructor
public class UserDTO {
    //用户ID（由服务端从令牌中写入，前端无需传）
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
}
