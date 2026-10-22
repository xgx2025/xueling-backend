package com.hope.xueling.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_feedback")
public class Feedback {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String requestId;
    private String category;
    private String subcategory;
    private String content;
    private String sourcePage;
    private String contextWord;
    private String contact;
    private String status;
    private String adminReply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime repliedAt;
}
