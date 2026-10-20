package com.hope.xueling.english.vocab.service;

import com.hope.xueling.english.vocab.domain.dto.SubmitStudyReviewDTO;
import com.hope.xueling.english.vocab.domain.dto.UpdateStudySettingsDTO;
import com.hope.xueling.english.vocab.domain.vo.StudyReviewResultVO;
import com.hope.xueling.english.vocab.domain.vo.StudySettingsVO;
import com.hope.xueling.english.vocab.domain.vo.StudyStatsVO;
import com.hope.xueling.english.vocab.domain.vo.TodayStudyVO;

public interface IStudyService {
    TodayStudyVO today(long userId);
    StudyReviewResultVO submitReview(long userId, SubmitStudyReviewDTO request);
    StudyStatsVO stats(long userId);
    StudySettingsVO settings(long userId);
    StudySettingsVO updateSettings(long userId, UpdateStudySettingsDTO request);
    void setBookEnabled(long userId, long bookId, boolean enabled);
}
