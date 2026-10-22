package com.hope.xueling.common.domain.vo;

import com.hope.xueling.common.domain.entity.Feedback;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackVO {
    private String id;
    private String userId;
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

    public static FeedbackVO from(Feedback feedback) {
        FeedbackVO vo = new FeedbackVO();
        vo.setId(String.valueOf(feedback.getId()));
        vo.setUserId(String.valueOf(feedback.getUserId()));
        vo.setCategory(feedback.getCategory());
        vo.setSubcategory(feedback.getSubcategory());
        vo.setContent(feedback.getContent());
        vo.setSourcePage(feedback.getSourcePage());
        vo.setContextWord(feedback.getContextWord());
        vo.setContact(feedback.getContact());
        vo.setStatus(feedback.getStatus());
        vo.setAdminReply(feedback.getAdminReply());
        vo.setCreateTime(feedback.getCreateTime());
        vo.setUpdateTime(feedback.getUpdateTime());
        vo.setRepliedAt(feedback.getRepliedAt());
        return vo;
    }
}
