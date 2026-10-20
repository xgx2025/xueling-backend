package com.hope.xueling.english.vocab.service;

import com.hope.xueling.common.exception.BusinessException;
import com.hope.xueling.english.vocab.domain.dto.SubmitStudyReviewDTO;
import com.hope.xueling.english.vocab.domain.vo.StudyReviewResultVO;
import com.hope.xueling.english.vocab.domain.vo.StudySettingsVO;
import com.hope.xueling.english.vocab.mapper.StudyMapper;
import com.hope.xueling.english.vocab.service.impl.StudyServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StudyServiceTest {
    private final StudyMapper mapper = mock(StudyMapper.class);
    private final ReviewScheduler scheduler = mock(ReviewScheduler.class);
    private final StudyServiceImpl service = new StudyServiceImpl(mapper, scheduler);

    private SubmitStudyReviewDTO review() {
        SubmitStudyReviewDTO request = new SubmitStudyReviewDTO();
        request.setWordId("123");
        request.setRating(3);
        request.setExpectedVersion(0);
        request.setRequestId(UUID.randomUUID().toString());
        return request;
    }

    @Test
    void retryWithSameRequestIdReturnsOriginalResultWithoutSchedulingAgain() {
        SubmitStudyReviewDTO request = review();
        StudyReviewResultVO result = new StudyReviewResultVO("123", "LEARNING",
                Instant.parse("2026-10-20T02:10:00Z"), 1);
        when(mapper.settings(7L, true)).thenReturn(new StudySettingsVO("Asia/Shanghai", 15, 0.9));
        when(mapper.reviewByRequestId(7L, request.getRequestId()))
                .thenReturn(new StudyMapper.PreviousReview(123L, 3, result));

        assertSame(result, service.submitReview(7L, request));
        verifyNoInteractions(scheduler);
        verify(mapper, never()).insertReview(anyLong(), anyLong(), anyLong(), anyInt(),
                any(), any(), any(), any(), anyString(), anyDouble(), anyString(), anyInt(), anyString());
    }

    @Test
    void dailyNewLimitRejectsAnotherNewCard() {
        SubmitStudyReviewDTO request = review();
        when(mapper.settings(7L, true)).thenReturn(new StudySettingsVO("Asia/Shanghai", 15, 0.9));
        when(mapper.eligibleNewWord(7L, 123L)).thenReturn(true);
        when(mapper.countIntroduced(eq(7L), any(LocalDate.class))).thenReturn(15);

        assertThrows(BusinessException.class, () -> service.submitReview(7L, request));
        verifyNoInteractions(scheduler);
    }
}
