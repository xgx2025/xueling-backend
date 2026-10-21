package com.hope.xueling.english.vocab.service;

import com.hope.xueling.english.vocab.domain.dto.SubmitStudyReviewDTO;
import com.hope.xueling.english.vocab.domain.dto.UpdateStudySettingsDTO;
import com.hope.xueling.english.vocab.domain.vo.StudyReviewResultVO;
import com.hope.xueling.english.vocab.domain.vo.StudySettingsVO;
import com.hope.xueling.english.vocab.domain.vo.StudyStatsVO;
import com.hope.xueling.english.vocab.domain.vo.StudyDayStatsVO;
import com.hope.xueling.english.vocab.domain.vo.StudyReviewCountVO;
import com.hope.xueling.english.vocab.domain.vo.TodayStudyVO;
import com.hope.xueling.english.vocab.domain.vo.StudyFamiliarityVO;
import java.util.List;

public interface IStudyService {
    TodayStudyVO today(long userId);
    StudyReviewResultVO submitReview(long userId, SubmitStudyReviewDTO request);
    StudyStatsVO stats(long userId);
    List<StudyDayStatsVO> history(long userId, int days);
    StudyReviewCountVO reviewCounts(long userId);
    StudyFamiliarityVO familiarity(long userId, Long bookId, String stage, int page);
    StudySettingsVO settings(long userId);
    StudySettingsVO updateSettings(long userId, UpdateStudySettingsDTO request);
    void setBookEnabled(long userId, long bookId, boolean enabled);
}
