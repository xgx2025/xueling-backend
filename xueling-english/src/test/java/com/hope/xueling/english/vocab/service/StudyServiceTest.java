package com.hope.xueling.english.vocab.service;

import com.hope.xueling.common.exception.BusinessException;
import com.hope.xueling.english.vocab.domain.dto.SubmitStudyReviewDTO;
import com.hope.xueling.english.vocab.domain.vo.StudyQueueCardVO;
import com.hope.xueling.english.vocab.domain.vo.StudyReviewResultVO;
import com.hope.xueling.english.vocab.domain.vo.StudySettingsVO;
import com.hope.xueling.english.vocab.mapper.StudyMapper;
import com.hope.xueling.english.vocab.service.impl.FsrsReviewScheduler;
import com.hope.xueling.english.vocab.service.impl.StudyServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StudyServiceTest {
    private static final long USER_ID = 7L;
    private final FakeStudyMapper mapper = new FakeStudyMapper();
    private final StudyServiceImpl service = new StudyServiceImpl(mapper, new FsrsReviewScheduler());

    private static class FakeStudyMapper extends StudyMapper {
        StudySettingsVO settings = new StudySettingsVO("Asia/Shanghai", 15, 0.9);
        PreviousReview previous;
        boolean eligible = true;
        int dueCount;
        int introduced;
        LocalDate introducedDate;
        int dueQueries;
        int newQueries;
        Instant dayStart;
        Instant dayEnd;
        boolean progressQueried;
        List<HistoryReviewRow> historyRows = List.of();

        FakeStudyMapper() { super(null); }
        @Override public void ensureSettings(long userId) {}
        @Override public void ensureSettingsForUpdate(long userId) {}
        @Override public StudySettingsVO settings(long userId, boolean lock) { return settings; }
        @Override public PreviousReview reviewByRequestId(long userId, String requestId) { return previous; }
        @Override public ProgressRow progressForUpdate(long userId, long wordId) {
            progressQueried = true;
            return null;
        }
        @Override public boolean eligibleNewWord(long userId, long wordId) { return eligible; }
        @Override public int countDue(long userId, Instant now) { return dueCount; }
        @Override public int countIntroduced(long userId, LocalDate date) {
            introducedDate = date;
            return introduced;
        }
        @Override public int countTodayReviews(long userId, Instant start, Instant end) {
            dayStart = start;
            dayEnd = end;
            return 0;
        }
        @Override public int countDueAttempts(long userId, Instant start, Instant end) { return 0; }
        @Override public int countDueSuccesses(long userId, Instant start, Instant end) { return 0; }
        @Override public int countLearned(long userId) { return 0; }
        @Override public Instant nextDue(long userId, Instant now) { return null; }
        @Override public List<HistoryReviewRow> historyReviews(long userId, Instant start, Instant end) {
            return historyRows;
        }
        @Override public int dueBacklogAt(long userId, Instant cutoff) { return 0; }
        @Override public List<StudyQueueCardVO> dueCards(long userId, Instant now, int limit) {
            dueQueries++;
            return List.of();
        }
        @Override public List<StudyQueueCardVO> newCards(long userId, int limit) {
            newQueries++;
            return List.of();
        }
    }

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
        StudyReviewResultVO result = new StudyReviewResultVO("123", "LEARNING",
                Instant.parse("2026-10-20T02:10:00Z"), 1);
        mapper.previous = new StudyMapper.PreviousReview(123L, 3, result);

        assertSame(result, service.submitReview(USER_ID, review()));
        assertFalse(mapper.progressQueried);
    }

    @Test
    void dailyNewLimitRejectsAnotherNewCard() {
        mapper.introduced = 15;

        assertThrows(BusinessException.class, () -> service.submitReview(USER_ID, review()));
    }

    @Test
    void recallRateHasNoValueWithoutFormalDueReviews() {
        var stats = service.stats(USER_ID);

        assertEquals(0, stats.getDueRecallAttempts());
        assertNull(stats.getDueRecallRate());
    }

    @Test
    void overdueCardsComeBeforeNewWordsAndBacklogPausesNewWords() {
        Instant instant = Instant.parse("2026-10-20T08:00:00Z");
        mapper.dueCount = 30;
        StudyServiceImpl fixedService = new StudyServiceImpl(mapper, new FsrsReviewScheduler(),
                Clock.fixed(instant, ZoneOffset.UTC));

        var today = fixedService.today(USER_ID);

        assertEquals(30, today.getDueCount());
        assertEquals(0, today.getNewRemaining());
        assertEquals(1, mapper.dueQueries);
        assertEquals(0, mapper.newQueries);
    }

    @Test
    void userTimeZoneSetsDailyBoundaryAndNewWordAllowance() {
        Instant beforeMidnight = Instant.parse("2026-10-20T15:59:59Z");
        Instant afterMidnight = Instant.parse("2026-10-20T16:00:00Z");
        mapper.introduced = 15;
        var before = new StudyServiceImpl(mapper, new FsrsReviewScheduler(),
                Clock.fixed(beforeMidnight, ZoneOffset.UTC)).today(USER_ID);
        assertEquals(LocalDate.of(2026, 10, 20), mapper.introducedDate);
        assertEquals(Instant.parse("2026-10-19T16:00:00Z"), mapper.dayStart);
        assertEquals(afterMidnight, mapper.dayEnd);

        mapper.introduced = 0;
        var after = new StudyServiceImpl(mapper, new FsrsReviewScheduler(),
                Clock.fixed(afterMidnight, ZoneOffset.UTC)).today(USER_ID);
        assertEquals(LocalDate.of(2026, 10, 21), mapper.introducedDate);
        assertEquals(afterMidnight, mapper.dayStart);
        assertEquals(Instant.parse("2026-10-21T16:00:00Z"), mapper.dayEnd);
        assertEquals(0, before.getNewRemaining());
        assertEquals(15, after.getNewRemaining());
        assertEquals(1, mapper.newQueries);
    }

    @Test
    void historyCountsFormalDueRecallSeparatelyFromShortTermLearning() {
        Instant instant = Instant.parse("2026-10-20T12:00:00Z");
        mapper.historyRows = List.of(
                new StudyMapper.HistoryReviewRow(instant.minusSeconds(3600),
                        instant.minusSeconds(7200), "REVIEW", 1, 2000),
                new StudyMapper.HistoryReviewRow(instant.minusSeconds(1800),
                        instant.minusSeconds(1900), "LEARNING", 3, 3000),
                new StudyMapper.HistoryReviewRow(instant.minusSeconds(900),
                        null, null, 3, 1000));
        StudyServiceImpl fixedService = new StudyServiceImpl(mapper, new FsrsReviewScheduler(),
                Clock.fixed(instant, ZoneOffset.UTC));

        var history = fixedService.history(USER_ID, 7);
        var today = history.getLast();

        assertEquals(7, history.size());
        assertEquals(LocalDate.of(2026, 10, 20), today.date());
        assertEquals(3, today.reviewCount());
        assertEquals(1, today.newWords());
        assertEquals(6000, today.studyTimeMs());
        assertEquals(1, today.dueRecallAttempts());
        assertEquals(0.0, today.dueRecallRate());
    }
}
