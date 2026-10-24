package com.hope.xueling.common.domain.dto;

import lombok.Data;

/**
 * 管理端处理用户反馈入参
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
public class UpdateFeedbackDTO {
    /**
     * 处理状态：PENDING 待处理、PROCESSING 处理中、REPLIED 已回复、CLOSED 已关闭
     */
    private String status;
    /**
     * 管理员回复内容，置为已回复时必填
     */
    private String adminReply;
}
