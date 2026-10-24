package com.hope.xueling.common.controller;

import com.hope.xueling.common.domain.dto.CreateFeedbackDTO;
import com.hope.xueling.common.domain.vo.FeedbackVO;
import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.common.service.IFeedbackService;
import com.hope.xueling.common.util.ThreadLocalUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户反馈控制器：提交反馈、查询我的反馈列表与反馈详情，用户身份一律取自登录令牌
 * @author 谢光湘
 * @since 2026/10/24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {
    private final IFeedbackService feedbackService;

    /**
     * 从当前请求的令牌上下文中取用户 ID
     * @return 当前登录用户 ID
     */
    private long userId() {
        Claims claims = ThreadLocalUtils.get();
        return claims.get("userId", Long.class);
    }

    /**
     * 提交一条反馈，requestId 用于幂等去重，重复提交返回首次创建的结果
     * @param request 反馈内容与来源信息
     * @return 创建后的反馈
     */
    @PostMapping
    public Result<FeedbackVO> create(@RequestBody CreateFeedbackDTO request) {
        return Result.success(feedbackService.create(userId(), request));
    }

    /**
     * 分页查询当前用户提交的反馈
     * @param page 页码，从 1 开始，默认 1
     * @return 当前页反馈列表
     */
    @GetMapping("/mine")
    public Result<List<FeedbackVO>> mine(@RequestParam(defaultValue = "1") int page) {
        return Result.success(feedbackService.mine(userId(), page));
    }

    /**
     * 查询当前用户的指定反馈详情，非本人反馈按不存在处理
     * @param id 反馈 ID
     * @return 反馈详情
     */
    @GetMapping("/mine/{id}")
    public Result<FeedbackVO> findMine(@PathVariable long id) {
        return Result.success(feedbackService.findMine(userId(), id));
    }

}
