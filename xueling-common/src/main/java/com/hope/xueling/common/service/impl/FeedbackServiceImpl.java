package com.hope.xueling.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hope.xueling.common.domain.dto.CreateFeedbackDTO;
import com.hope.xueling.common.domain.dto.UpdateFeedbackDTO;
import com.hope.xueling.common.domain.entity.Feedback;
import com.hope.xueling.common.domain.vo.FeedbackVO;
import com.hope.xueling.common.exception.BusinessException;
import com.hope.xueling.common.exception.ForbiddenException;
import com.hope.xueling.common.exception.ValidationException;
import com.hope.xueling.common.mapper.FeedbackMapper;
import com.hope.xueling.common.security.AdminAccessPolicy;
import com.hope.xueling.common.service.IFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements IFeedbackService {
    private static final int PAGE_SIZE = 20;
    private static final Set<String> CATEGORIES = Set.of("FUNCTION_ISSUE", "FEATURE_IDEA", "EXPERIENCE", "OTHER", "TRANSLATION");
    private static final Set<String> STATUSES = Set.of("PENDING", "PROCESSING", "REPLIED", "CLOSED");
    private static final Set<String> TRANSLATION_TYPES = Set.of("meaning", "sound", "example", "other");

    private final FeedbackMapper feedbackMapper;
    private final AdminAccessPolicy adminAccessPolicy;

    @Override
    public FeedbackVO create(long userId, CreateFeedbackDTO request) {
        if (request == null) throw new ValidationException("请填写反馈内容");
        String requestId = required(request.getRequestId(), 64, "请求编号");
        if (!requestId.matches("[0-9a-fA-F-]{36}")) throw new ValidationException("请求编号格式有误");
        Feedback existing = findByRequestId(userId, requestId);
        if (existing != null) return FeedbackVO.from(existing);

        String category = required(request.getCategory(), 32, "反馈类型");
        if (!CATEGORIES.contains(category)) throw new ValidationException("反馈类型无效");
        String content = required(request.getContent(), 2000, "反馈内容");
        if (content.length() < 5) throw new ValidationException("请至少填写 5 个字的反馈内容");
        String subcategory = optional(request.getSubcategory(), 32, "问题类型");
        if ("TRANSLATION".equals(category)) {
            if (subcategory == null || !TRANSLATION_TYPES.contains(subcategory)) throw new ValidationException("请选择翻译问题类型");
        } else if (subcategory != null) {
            throw new ValidationException("此反馈类型无需问题子类");
        }
        String sourcePage = optional(request.getSourcePage(), 255, "来源页面");
        if (sourcePage != null && (!sourcePage.startsWith("/") || sourcePage.startsWith("//"))) {
            throw new ValidationException("来源页面格式有误");
        }
        String contextWord = optional(request.getContextWord(), 100, "关联单词");
        String contact = optional(request.getContact(), 100, "联系方式");
        if (feedbackMapper.countRecent(userId) >= 5) throw new BusinessException("提交太频繁，请稍后再试");

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setRequestId(requestId);
        feedback.setCategory(category);
        feedback.setSubcategory(subcategory);
        feedback.setContent(content);
        feedback.setSourcePage(sourcePage);
        feedback.setContextWord(contextWord);
        feedback.setContact(contact);
        feedback.setStatus("PENDING");
        feedback.setCreateTime(LocalDateTime.now());
        feedback.setUpdateTime(feedback.getCreateTime());
        try {
            feedbackMapper.insert(feedback);
        } catch (DuplicateKeyException e) {
            Feedback duplicate = findByRequestId(userId, requestId);
            if (duplicate == null) throw e;
            return FeedbackVO.from(duplicate);
        }
        return FeedbackVO.from(feedback);
    }

    @Override
    public List<FeedbackVO> mine(long userId, int page) {
        return feedbackMapper.listByUser(userId, PAGE_SIZE, offset(page)).stream().map(FeedbackVO::from).toList();
    }

    @Override
    public FeedbackVO findMine(long userId, long feedbackId) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null || !feedback.getUserId().equals(userId)) throw new BusinessException("反馈不存在");
        return FeedbackVO.from(feedback);
    }

    @Override
    public boolean isAdmin(long userId) {
        return adminAccessPolicy.isAdmin(userId);
    }

    @Override
    public List<FeedbackVO> listForAdmin(long userId, String status, int page) {
        requireAdmin(userId);
        if (status != null && !STATUSES.contains(status)) throw new ValidationException("处理状态无效");
        return feedbackMapper.listForAdmin(status, PAGE_SIZE, offset(page)).stream().map(FeedbackVO::from).toList();
    }

    @Override
    public FeedbackVO update(long userId, long feedbackId, UpdateFeedbackDTO request) {
        requireAdmin(userId);
        if (request == null) throw new ValidationException("请填写处理结果");
        String status = required(request.getStatus(), 20, "处理状态");
        if (!STATUSES.contains(status)) throw new ValidationException("处理状态无效");
        String reply = optional(request.getAdminReply(), 2000, "处理回复");
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) throw new BusinessException("反馈不存在");
        if ("REPLIED".equals(status) && reply == null && feedback.getAdminReply() == null) {
            throw new ValidationException("回复用户时请填写处理说明");
        }
        feedback.setStatus(status);
        if (reply != null) {
            feedback.setAdminReply(reply);
            feedback.setRepliedAt(LocalDateTime.now());
        }
        feedback.setUpdateTime(LocalDateTime.now());
        feedbackMapper.updateById(feedback);
        return FeedbackVO.from(feedback);
    }

    private Feedback findByRequestId(long userId, String requestId) {
        return feedbackMapper.selectOne(new LambdaQueryWrapper<Feedback>()
                .eq(Feedback::getUserId, userId).eq(Feedback::getRequestId, requestId));
    }

    private void requireAdmin(long userId) {
        if (!isAdmin(userId)) throw new ForbiddenException("无权处理用户反馈");
    }

    private static int offset(int page) {
        if (page < 1 || page > 1000) throw new ValidationException("页码超出范围");
        return (page - 1) * PAGE_SIZE;
    }

    private static String required(String value, int max, String label) {
        String trimmed = optional(value, max, label);
        if (trimmed == null) throw new ValidationException("请填写" + label);
        return trimmed;
    }

    private static String optional(String value, int max, String label) {
        if (value == null || value.isBlank()) return null;
        String trimmed = value.trim();
        if (trimmed.length() > max) throw new ValidationException(label + "不能超过 " + max + " 字");
        return trimmed;
    }
}
