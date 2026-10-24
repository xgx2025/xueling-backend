package com.hope.xueling.common.service;

import com.hope.xueling.common.domain.dto.CreateFeedbackDTO;
import com.hope.xueling.common.domain.dto.UpdateFeedbackDTO;
import com.hope.xueling.common.domain.vo.FeedbackVO;

import java.util.List;

/**
 * 用户反馈服务接口：用户侧提交与查询、管理侧列表与处理，均为用户维度的权限校验
 * @author 谢光湘
 * @since 2026/10/24
 */
public interface IFeedbackService {
    /**
     * 创建反馈，按 requestId 幂等
     * @param userId 提交人用户 ID
     * @param request 反馈内容与来源信息
     * @return 创建后的反馈
     */
    FeedbackVO create(long userId, CreateFeedbackDTO request);
    /**
     * 分页查询指定用户提交的反馈
     * @param userId 用户 ID
     * @param page 页码，从 1 开始
     * @return 当前页反馈列表
     */
    List<FeedbackVO> mine(long userId, int page);
    /**
     * 查询指定用户的单条反馈
     * @param userId 用户 ID
     * @param feedbackId 反馈 ID
     * @return 反馈详情
     */
    FeedbackVO findMine(long userId, long feedbackId);
    /**
     * 判断用户是否具备管理端处理反馈的权限
     * @param userId 用户 ID
     * @return 是否为管理员
     */
    boolean isAdmin(long userId);
    /**
     * 管理端分页查询反馈，可按处理状态过滤
     * @param userId 操作人（管理员）用户 ID
     * @param status 处理状态过滤值，可为空
     * @param page 页码，从 1 开始
     * @return 当前页反馈列表
     */
    List<FeedbackVO> listForAdmin(long userId, String status, int page);
    /**
     * 管理端更新反馈处理状态与回复内容
     * @param userId 操作人（管理员）用户 ID
     * @param feedbackId 反馈 ID
     * @param request 处理状态与回复内容
     * @return 更新后的反馈
     */
    FeedbackVO update(long userId, long feedbackId, UpdateFeedbackDTO request);
}
