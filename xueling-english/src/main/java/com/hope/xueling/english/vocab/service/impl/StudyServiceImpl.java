package com.hope.xueling.english.vocab.service.impl;

import com.hope.xueling.common.exception.BusinessException;
import com.hope.xueling.common.exception.ValidationException;
import com.hope.xueling.english.vocab.domain.dto.SubmitStudyReviewDTO;
import com.hope.xueling.english.vocab.domain.dto.UpdateStudySettingsDTO;
import com.hope.xueling.english.vocab.domain.vo.*;
import com.hope.xueling.english.vocab.mapper.StudyMapper;
import com.hope.xueling.english.vocab.service.IStudyService;
import com.hope.xueling.english.vocab.service.ReviewScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class StudyServiceImpl implements IStudyService {
    private static final int PAGE_SIZE = 20;
    private final StudyMapper studyMapper;
    private final ReviewScheduler reviewScheduler;
    private final Clock clock;

    @Autowired
    public StudyServiceImpl(StudyMapper studyMapper, ReviewScheduler reviewScheduler) {
        this(studyMapper, reviewScheduler, Clock.systemUTC());
    }

    public StudyServiceImpl(StudyMapper studyMapper, ReviewScheduler reviewScheduler, Clock clock) {
        this.studyMapper = studyMapper;
        this.reviewScheduler = reviewScheduler;
        this.clock = clock;
    }

    private record DayWindow(LocalDate date, Instant start, Instant end) {}

    private DayWindow dayWindow(StudySettingsVO settings, Instant now) {
        ZoneId zone = ZoneId.of(settings.getTimeZone());
        LocalDate date = now.atZone(zone).toLocalDate();
        return new DayWindow(date, date.atStartOfDay(zone).toInstant(),
                date.plusDays(1).atStartOfDay(zone).toInstant());
    }

    private Instant now() {
        return clock.instant().truncatedTo(ChronoUnit.MILLIS);
    }

    @Override
    @Transactional
    public TodayStudyVO today(long userId) {
        studyMapper.ensureSettings(userId);
        StudySettingsVO settings = studyMapper.settings(userId, false);
        Instant now = now();
        DayWindow day = dayWindow(settings, now);
        int dueCount = studyMapper.countDue(userId, now);
        int introduced = studyMapper.countIntroduced(userId, day.date());
        int remaining = Math.max(0, settings.getDailyNewLimit() - introduced);
        if (dueCount >= 30) remaining = 0;
        List<StudyQueueCardVO> cards = dueCount > 0
                ? studyMapper.dueCards(userId, now, PAGE_SIZE)
                : remaining > 0 ? studyMapper.newCards(userId, Math.min(PAGE_SIZE, remaining)) : List.of();
        TodayStudyVO today = new TodayStudyVO();
        today.setDueCount(dueCount);
        today.setNewRemaining(remaining);
        today.setTodayNew(introduced);
        today.setTodayReviewed(studyMapper.countTodayReviews(userId, day.start(), day.end()));
        today.setNextDueAt(studyMapper.nextDue(userId, now));
        today.setCards(cards);
        return today;
    }

    @Override
    @Transactional
    public StudyReviewResultVO submitReview(long userId, SubmitStudyReviewDTO request) {
        if (request == null || request.getWordId() == null || request.getRating() == null
                || request.getRating() < 1 || request.getRating() > 4
                || request.getExpectedVersion() == null || request.getExpectedVersion() < 0
                || request.getRequestId() == null || request.getRequestId().length() > 64) {
            throw new ValidationException("复习参数无效");
        }
        try {
            UUID.fromString(request.getRequestId());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("请求ID必须是UUID");
        }
        if (request.getElapsedMs() != null && (request.getElapsedMs() < 0 || request.getElapsedMs() > 600_000)) {
            throw new ValidationException("用时超出范围");
        }
        long wordId;
        try {
            wordId = Long.parseLong(request.getWordId());
            if (wordId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new ValidationException("单词ID无效");
        }

        studyMapper.ensureSettingsForUpdate(userId);
        // 锁设置行，串行化同一用户的新词额度与评分提交。
        StudySettingsVO settings = studyMapper.settings(userId, true);
        StudyMapper.PreviousReview previous = studyMapper.reviewByRequestId(userId, request.getRequestId());
        if (previous != null) {
            if (previous.wordId() != wordId || previous.rating() != request.getRating()) {
                throw new ValidationException("请求ID已用于另一条评分");
            }
            return previous.result();
        }

        Instant reviewedAt = now();
        StudyMapper.ProgressRow progress = studyMapper.progressForUpdate(userId, wordId);
        int version = progress == null ? 0 : progress.version();
        if (version != request.getExpectedVersion()) {
            throw new BusinessException("卡片已在其他设备更新，请刷新今日学习");
        }
        DayWindow day = dayWindow(settings, reviewedAt);
        if (progress == null) {
            if (!studyMapper.eligibleNewWord(userId, wordId)) {
                throw new BusinessException("该单词不在已启用词书中");
            }
            if (studyMapper.countIntroduced(userId, day.date()) >= settings.getDailyNewLimit()) {
                throw new BusinessException("今日新词额度已用完");
            }
            if (studyMapper.countDue(userId, reviewedAt) >= 30) {
                throw new BusinessException("待复习单词较多，请先完成到期复习");
            }
        } else if (progress.dueAt().isAfter(reviewedAt)) {
            throw new BusinessException("该单词尚未到复习时间");
        }

        ReviewScheduler.ScheduledReview scheduled = reviewScheduler.review(
                progress == null ? null : progress.cardJson(), request.getRating(),
                settings.getDesiredRetention(), reviewedAt);
        int lapseCount = (progress == null ? 0 : progress.lapseCount())
                + (progress != null && "REVIEW".equals(progress.state()) && request.getRating() == 1 ? 1 : 0);
        if (progress == null) {
            studyMapper.insertProgress(userId, wordId, scheduled.cardJson(), scheduled.state(),
                    scheduled.dueAt(), reviewedAt, day.date(), reviewScheduler.version(), lapseCount);
            progress = studyMapper.progressForUpdate(userId, wordId);
        } else {
            studyMapper.updateProgress(progress.id(), scheduled.cardJson(), scheduled.state(),
                    scheduled.dueAt(), reviewedAt, progress.reviewCount() + 1, lapseCount,
                    reviewScheduler.version());
        }
        int versionAfter = version + 1;
        studyMapper.insertReview(progress.id(), userId, wordId, request.getRating(), reviewedAt,
                version == 0 ? null : progress.dueAt(), scheduled.dueAt(), request.getElapsedMs(),
                reviewScheduler.version(), settings.getDesiredRetention(), scheduled.state(),
                versionAfter, request.getRequestId());
        return new StudyReviewResultVO(String.valueOf(wordId), scheduled.state(), scheduled.dueAt(), versionAfter);
    }

    @Override
    @Transactional
    public StudyStatsVO stats(long userId) {
        studyMapper.ensureSettings(userId);
        StudySettingsVO settings = studyMapper.settings(userId, false);
        Instant now = now();
        DayWindow day = dayWindow(settings, now);
        int attempts = studyMapper.countDueAttempts(userId, day.start(), day.end());
        int successes = studyMapper.countDueSuccesses(userId, day.start(), day.end());
        return new StudyStatsVO(studyMapper.countTodayReviews(userId, day.start(), day.end()),
                studyMapper.countIntroduced(userId, day.date()), studyMapper.countLearned(userId),
                studyMapper.countDue(userId, now), attempts,
                attempts == 0 ? null : (double) successes / attempts);
    }

    private static class DayAggregate {
        int reviews;
        int newWords;
        long elapsedMs;
        int dueAttempts;
        int dueSuccesses;
    }

    @Override
    @Transactional
    public List<StudyDayStatsVO> history(long userId, int days) {
        if (days != 7 && days != 30) throw new ValidationException("仅支持近7天或30天");
        studyMapper.ensureSettings(userId);
        ZoneId zone = ZoneId.of(studyMapper.settings(userId, false).getTimeZone());
        Instant current = now();
        LocalDate today = current.atZone(zone).toLocalDate();
        LocalDate first = today.minusDays(days - 1L);
        Instant start = first.atStartOfDay(zone).toInstant();
        Instant end = today.plusDays(1).atStartOfDay(zone).toInstant();
        Map<LocalDate, DayAggregate> aggregates = new HashMap<>();
        for (StudyMapper.HistoryReviewRow row : studyMapper.historyReviews(userId, start, end)) {
            LocalDate date = row.reviewedAt().atZone(zone).toLocalDate();
            DayAggregate aggregate = aggregates.computeIfAbsent(date, ignored -> new DayAggregate());
            aggregate.reviews++;
            if (row.dueBefore() == null) aggregate.newWords++;
            aggregate.elapsedMs += row.elapsedMs() == null ? 0 : row.elapsedMs();
            if ("REVIEW".equals(row.stateBefore()) && row.dueBefore() != null
                    && !row.dueBefore().isAfter(row.reviewedAt())) {
                aggregate.dueAttempts++;
                if (row.rating() > 1) aggregate.dueSuccesses++;
            }
        }
        List<StudyDayStatsVO> result = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            LocalDate date = first.plusDays(i);
            DayAggregate aggregate = aggregates.getOrDefault(date, new DayAggregate());
            Instant cutoff = date.equals(today) ? current : date.plusDays(1).atStartOfDay(zone).toInstant();
            result.add(new StudyDayStatsVO(date, aggregate.reviews, aggregate.newWords,
                    aggregate.elapsedMs, aggregate.dueAttempts,
                    aggregate.dueAttempts == 0 ? null : (double) aggregate.dueSuccesses / aggregate.dueAttempts,
                    studyMapper.dueBacklogAt(userId, cutoff)));
        }
        return result;
    }

    @Override
    @Transactional
    public StudyReviewCountVO reviewCounts(long userId) {
        studyMapper.ensureSettings(userId);
        return studyMapper.reviewCounts(userId);
    }

    @Override
    @Transactional
    public StudySettingsVO settings(long userId) {
        studyMapper.ensureSettings(userId);
        return studyMapper.settings(userId, false);
    }

    @Override
    @Transactional
    public StudySettingsVO updateSettings(long userId, UpdateStudySettingsDTO request) {
        if (request == null || request.getTimeZone() == null || request.getDailyNewLimit() == null
                || request.getDesiredRetention() == null || request.getDailyNewLimit() < 0
                || request.getDailyNewLimit() > 100 || !Double.isFinite(request.getDesiredRetention())
                || request.getDesiredRetention() < 0.8 || request.getDesiredRetention() > 0.97) {
            throw new ValidationException("学习设置无效");
        }
        try {
            ZoneId.of(request.getTimeZone());
        } catch (DateTimeException e) {
            throw new ValidationException("时区无效");
        }
        studyMapper.ensureSettingsForUpdate(userId);
        StudySettingsVO settings = new StudySettingsVO(request.getTimeZone(), request.getDailyNewLimit(),
                request.getDesiredRetention());
        studyMapper.updateSettings(userId, settings);
        return settings;
    }

    @Override
    @Transactional
    public void setBookEnabled(long userId, long bookId, boolean enabled) {
        if (!studyMapper.accessibleBook(userId, bookId)) {
            throw new BusinessException("词书不存在或无权访问");
        }
        studyMapper.ensureSettingsForUpdate(userId);
        studyMapper.setBookEnabled(userId, bookId, enabled);
    }
}
