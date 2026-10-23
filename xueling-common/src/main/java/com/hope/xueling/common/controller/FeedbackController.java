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

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {
    private final IFeedbackService feedbackService;

    private long userId() {
        Claims claims = ThreadLocalUtils.get();
        return claims.get("userId", Long.class);
    }

    @PostMapping
    public Result<FeedbackVO> create(@RequestBody CreateFeedbackDTO request) {
        return Result.success(feedbackService.create(userId(), request));
    }

    @GetMapping("/mine")
    public Result<List<FeedbackVO>> mine(@RequestParam(defaultValue = "1") int page) {
        return Result.success(feedbackService.mine(userId(), page));
    }

    @GetMapping("/mine/{id}")
    public Result<FeedbackVO> findMine(@PathVariable long id) {
        return Result.success(feedbackService.findMine(userId(), id));
    }

}
