package com.hope.xueling.english.vocab.controller;

import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.common.exception.ValidationException;
import com.hope.xueling.common.util.ThreadLocalUtils;
import com.hope.xueling.english.vocab.domain.dto.SubmitStudyReviewDTO;
import com.hope.xueling.english.vocab.domain.dto.UpdateStudyBookDTO;
import com.hope.xueling.english.vocab.domain.dto.UpdateStudySettingsDTO;
import com.hope.xueling.english.vocab.domain.vo.*;
import com.hope.xueling.english.vocab.service.IStudyService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyController {
    private final IStudyService studyService;

    private long userId() {
        Claims claims = ThreadLocalUtils.get();
        return claims.get("userId", Long.class);
    }

    @GetMapping("/today")
    public Result<TodayStudyVO> today() {
        return Result.success(studyService.today(userId()));
    }

    @PostMapping("/reviews")
    public Result<StudyReviewResultVO> submitReview(@RequestBody SubmitStudyReviewDTO request) {
        return Result.success(studyService.submitReview(userId(), request));
    }

    @GetMapping("/stats")
    public Result<StudyStatsVO> stats() {
        return Result.success(studyService.stats(userId()));
    }

    @GetMapping("/history")
    public Result<List<StudyDayStatsVO>> history(@RequestParam(defaultValue = "7") int days) {
        return Result.success(studyService.history(userId(), days));
    }

    @GetMapping("/review-counts")
    public Result<StudyReviewCountVO> reviewCounts() {
        return Result.success(studyService.reviewCounts(userId()));
    }

    @GetMapping("/familiarity")
    public Result<StudyFamiliarityVO> familiarity(@RequestParam(required = false) Long bookId,
                                                  @RequestParam(defaultValue = "ALL") String stage,
                                                  @RequestParam(defaultValue = "1") int page) {
        return Result.success(studyService.familiarity(userId(), bookId, stage, page));
    }

    @GetMapping("/settings")
    public Result<StudySettingsVO> settings() {
        return Result.success(studyService.settings(userId()));
    }

    @PutMapping("/settings")
    public Result<StudySettingsVO> updateSettings(@RequestBody UpdateStudySettingsDTO request) {
        return Result.success(studyService.updateSettings(userId(), request));
    }

    @PutMapping("/books/{bookId}")
    public Result<String> setBookEnabled(@PathVariable long bookId, @RequestBody UpdateStudyBookDTO request) {
        if (request == null || request.getEnabled() == null) throw new ValidationException("请选择词书状态");
        studyService.setBookEnabled(userId(), bookId, request.getEnabled());
        return Result.success("学习词书已更新");
    }
}
