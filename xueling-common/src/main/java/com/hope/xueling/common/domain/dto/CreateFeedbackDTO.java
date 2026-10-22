package com.hope.xueling.common.domain.dto;

import lombok.Data;

@Data
public class CreateFeedbackDTO {
    private String requestId;
    private String category;
    private String subcategory;
    private String content;
    private String sourcePage;
    private String contextWord;
    private String contact;
}
