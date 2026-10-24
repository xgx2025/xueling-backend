package com.hope.xueling.common.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 登录数据传输对象
 * @author 谢光湘
 * @since 2026/1/20
 */

@Data
@NoArgsConstructor
public class LoginDTO {
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
     * IP地址
     */
    private String ip;
}
