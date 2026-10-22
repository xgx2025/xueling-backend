package com.hope.xueling.common.service;

import com.hope.xueling.common.domain.dto.CreateFeedbackDTO;
import com.hope.xueling.common.domain.dto.UpdateFeedbackDTO;
import com.hope.xueling.common.domain.vo.FeedbackVO;

import java.util.List;

public interface IFeedbackService {
    FeedbackVO create(long userId, CreateFeedbackDTO request);
    List<FeedbackVO> mine(long userId, int page);
    FeedbackVO findMine(long userId, long feedbackId);
    boolean isAdmin(long userId);
    List<FeedbackVO> listForAdmin(long userId, String status, int page);
    FeedbackVO update(long userId, long feedbackId, UpdateFeedbackDTO request);
}
