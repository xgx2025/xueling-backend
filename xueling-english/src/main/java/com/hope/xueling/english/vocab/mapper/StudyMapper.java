package com.hope.xueling.english.vocab.mapper;

import com.hope.xueling.english.vocab.domain.vo.StudyQueueCardVO;
import com.hope.xueling.english.vocab.domain.vo.StudySettingsVO;
import com.hope.xueling.english.vocab.domain.vo.StudyReviewResultVO;
import com.hope.xueling.english.vocab.domain.vo.StudyReviewCountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StudyMapper {
    private final JdbcTemplate jdbc;

    public record ProgressRow(long id, String cardJson, String state, Instant dueAt,
                              int version, int reviewCount, int lapseCount) {}

    public record PreviousReview(long wordId, int rating, StudyReviewResultVO result) {}
    public record HistoryReviewRow(Instant reviewedAt, Instant dueBefore,
                                   String stateBefore, int rating, Integer elapsedMs) {}

    private static Instant utc(ResultSet rs, String column) throws SQLException {
        LocalDateTime value = rs.getObject(column, LocalDateTime.class);
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }

    private static LocalDateTime dbTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static StudyQueueCardVO queueCard(ResultSet rs, boolean isNew) throws SQLException {
        StudyQueueCardVO card = new StudyQueueCardVO();
        card.setWordId(String.valueOf(rs.getLong("word_id")));
        card.setWord(rs.getString("word"));
        card.setMeaning(rs.getString("meaning"));
        card.setPhonetic(rs.getString("phonetic"));
        card.setExample(rs.getString("example"));
        card.setExampleTranslation(rs.getString("example_translation"));
        card.setImageUrl(rs.getString("image_url"));
        card.setNewCard(isNew);
        card.setVersion(isNew ? 0 : rs.getInt("version"));
        card.setState(isNew ? "NEW" : rs.getString("state"));
        card.setDueAt(isNew ? null : utc(rs, "due_at"));
        return card;
    }

    public void ensureSettings(long userId) {
        jdbc.update("INSERT IGNORE INTO user_study_settings (user_id) VALUES (?)", userId);
        jdbc.update("INSERT IGNORE INTO user_study_book (user_id, book_id) "
                + "SELECT ?, id FROM word_book WHERE system_key = 'CET4' AND is_deleted = 0", userId);
    }

    public void ensureSettingsForUpdate(long userId) {
        // 写入路径先取得独占锁，避免两个 INSERT IGNORE 的共享锁同时升级为 FOR UPDATE 时死锁。
        jdbc.update("INSERT INTO user_study_settings (user_id) VALUES (?) "
                + "ON DUPLICATE KEY UPDATE user_id=user_id", userId);
        jdbc.update("INSERT IGNORE INTO user_study_book (user_id, book_id) "
                + "SELECT ?, id FROM word_book WHERE system_key = 'CET4' AND is_deleted = 0", userId);
    }

    public StudySettingsVO settings(long userId, boolean lock) {
        String sql = "SELECT time_zone, daily_new_limit, desired_retention FROM user_study_settings WHERE user_id = ?"
                + (lock ? " FOR UPDATE" : "");
        return jdbc.queryForObject(sql, (rs, rowNum) -> new StudySettingsVO(
                rs.getString("time_zone"), rs.getInt("daily_new_limit"),
                rs.getDouble("desired_retention")), userId);
    }

    public void updateSettings(long userId, StudySettingsVO settings) {
        jdbc.update("UPDATE user_study_settings SET time_zone=?, daily_new_limit=?, desired_retention=? WHERE user_id=?",
                settings.getTimeZone(), settings.getDailyNewLimit(), settings.getDesiredRetention(), userId);
    }

    public int countDue(long userId, Instant now) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM word_learning_progress "
                + "WHERE user_id=? AND card_type='EN_ZH' AND due_at<=?", Integer.class, userId, dbTime(now));
    }

    public Instant nextDue(long userId, Instant now) {
        LocalDateTime value = jdbc.query("SELECT MIN(due_at) FROM word_learning_progress "
                        + "WHERE user_id=? AND card_type='EN_ZH' AND due_at>?",
                rs -> rs.next() ? rs.getObject(1, LocalDateTime.class) : null,
                userId, dbTime(now));
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }

    public List<StudyQueueCardVO> dueCards(long userId, Instant now, int limit) {
        return jdbc.query("SELECT d.id AS word_id,d.word,d.meaning,d.phonetic,d.example,"
                + "d.example_translation,d.image_url,p.state,p.due_at,p.version "
                + "FROM word_learning_progress p JOIN word_dictionary d ON d.id=p.word_id "
                + "WHERE p.user_id=? AND p.card_type='EN_ZH' AND p.due_at<=? "
                + "ORDER BY p.due_at,p.id LIMIT ?",
                (rs, rowNum) -> queueCard(rs, false), userId, dbTime(now), limit);
    }

    public List<StudyQueueCardVO> newCards(long userId, int limit) {
        return jdbc.query("SELECT d.id AS word_id,d.word,d.meaning,d.phonetic,d.example,"
                + "d.example_translation,d.image_url "
                + "FROM user_study_book s JOIN word_book b ON b.id=s.book_id "
                + "JOIN word_book_dictionary_relation r ON r.word_book_id=b.id "
                + "JOIN word_dictionary d ON d.id=r.word_id "
                + "LEFT JOIN word_learning_progress p ON p.user_id=s.user_id AND p.word_id=d.id AND p.card_type='EN_ZH' "
                + "WHERE s.user_id=? AND s.enabled=1 AND b.is_deleted=0 "
                + "AND (b.book_type='SYSTEM' OR b.user_id=s.user_id) AND p.id IS NULL "
                + "AND d.meaning IS NOT NULL AND d.meaning<>'' "
                + "GROUP BY d.id ORDER BY MIN(s.priority),MIN(r.sort_order),d.id LIMIT ?",
                (rs, rowNum) -> queueCard(rs, true), userId, limit);
    }

    public int countIntroduced(long userId, LocalDate date) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM word_learning_progress "
                + "WHERE user_id=? AND introduced_date=? AND card_type='EN_ZH'", Integer.class, userId, date);
    }

    public int countTodayReviews(long userId, Instant start, Instant end) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM word_review_record "
                + "WHERE user_id=? AND reviewed_at>=? AND reviewed_at<?", Integer.class,
                userId, dbTime(start), dbTime(end));
    }

    public List<HistoryReviewRow> historyReviews(long userId, Instant start, Instant end) {
        return jdbc.query("SELECT r.reviewed_at,r.due_before,r.rating,r.elapsed_ms,"
                        + "(SELECT previous_review.state_after FROM word_review_record previous_review "
                        + "WHERE previous_review.progress_id=r.progress_id AND previous_review.id<r.id "
                        + "ORDER BY previous_review.id DESC LIMIT 1) AS state_before "
                        + "FROM word_review_record r WHERE r.user_id=? AND r.reviewed_at>=? AND r.reviewed_at<? "
                        + "ORDER BY r.reviewed_at,r.id",
                (rs, rowNum) -> new HistoryReviewRow(utc(rs, "reviewed_at"), utc(rs, "due_before"),
                        rs.getString("state_before"), rs.getInt("rating"),
                        rs.getObject("elapsed_ms", Integer.class)), userId, dbTime(start), dbTime(end));
    }

    public int dueBacklogAt(long userId, Instant cutoff) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM word_review_record r JOIN "
                        + "(SELECT progress_id,MAX(id) AS last_id FROM word_review_record "
                        + "WHERE user_id=? AND reviewed_at<? GROUP BY progress_id) latest ON r.id=latest.last_id "
                        + "WHERE r.due_after<?", Integer.class,
                userId, dbTime(cutoff), dbTime(cutoff));
    }

    public ProgressRow progressForUpdate(long userId, long wordId) {
        List<ProgressRow> rows = jdbc.query("SELECT id,card_json,state,due_at,version,review_count,lapse_count "
                + "FROM word_learning_progress WHERE user_id=? AND word_id=? AND card_type='EN_ZH' FOR UPDATE",
                (rs, rowNum) -> new ProgressRow(rs.getLong("id"), rs.getString("card_json"),
                        rs.getString("state"), utc(rs, "due_at"), rs.getInt("version"),
                        rs.getInt("review_count"), rs.getInt("lapse_count")), userId, wordId);
        return rows.isEmpty() ? null : rows.getFirst();
    }

    public boolean eligibleNewWord(long userId, long wordId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM user_study_book s "
                + "JOIN word_book b ON b.id=s.book_id "
                + "JOIN word_book_dictionary_relation r ON r.word_book_id=b.id "
                + "JOIN word_dictionary d ON d.id=r.word_id "
                + "WHERE s.user_id=? AND s.enabled=1 AND b.is_deleted=0 "
                + "AND (b.book_type='SYSTEM' OR b.user_id=s.user_id) AND r.word_id=? "
                + "AND d.meaning IS NOT NULL AND d.meaning<>''",
                Integer.class, userId, wordId);
        return count != null && count > 0;
    }

    public void insertProgress(long userId, long wordId, String cardJson, String state,
                               Instant dueAt, Instant reviewedAt, LocalDate date, String schedulerVersion,
                               int lapseCount) {
        jdbc.update("INSERT INTO word_learning_progress (user_id,word_id,card_type,state,card_json,due_at,"
                        + "last_review_at,introduced_date,review_count,lapse_count,scheduler_version,version) "
                        + "VALUES (?,?,'EN_ZH',?,?,?,?,?,1,?,?,1)",
                userId, wordId, state, cardJson, dbTime(dueAt), dbTime(reviewedAt), date,
                lapseCount, schedulerVersion);
    }

    public void updateProgress(long progressId, String cardJson, String state, Instant dueAt,
                               Instant reviewedAt, int reviewCount, int lapseCount, String schedulerVersion) {
        jdbc.update("UPDATE word_learning_progress SET card_json=?,state=?,due_at=?,last_review_at=?,"
                        + "review_count=?,lapse_count=?,scheduler_version=?,version=version+1 WHERE id=?",
                cardJson, state, dbTime(dueAt), dbTime(reviewedAt), reviewCount, lapseCount,
                schedulerVersion, progressId);
    }

    public PreviousReview reviewByRequestId(long userId, String requestId) {
        List<PreviousReview> rows = jdbc.query("SELECT word_id,rating,state_after,due_after,version_after "
                + "FROM word_review_record WHERE user_id=? AND request_id=?",
                (rs, rowNum) -> new PreviousReview(rs.getLong("word_id"), rs.getInt("rating"),
                        new StudyReviewResultVO(String.valueOf(rs.getLong("word_id")),
                                rs.getString("state_after"), utc(rs, "due_after"),
                                rs.getInt("version_after"))), userId, requestId);
        return rows.isEmpty() ? null : rows.getFirst();
    }

    public void insertReview(long progressId, long userId, long wordId, int rating,
                             Instant reviewedAt, Instant dueBefore, Instant dueAfter, Integer elapsedMs,
                             String schedulerVersion, double retention, String stateAfter, int versionAfter,
                             String requestId) {
        jdbc.update("INSERT INTO word_review_record (progress_id,user_id,word_id,card_type,rating,reviewed_at,"
                        + "due_before,due_after,elapsed_ms,scheduler_version,desired_retention,state_after,"
                        + "version_after,request_id) VALUES (?,?,?,'EN_ZH',?,?,?,?,?,?,?,?,?,?)",
                progressId, userId, wordId, rating, dbTime(reviewedAt),
                dueBefore == null ? null : dbTime(dueBefore), dbTime(dueAfter), elapsedMs,
                schedulerVersion, retention, stateAfter, versionAfter, requestId);
    }

    public int countLearned(long userId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM word_learning_progress WHERE user_id=? AND card_type='EN_ZH'",
                Integer.class, userId);
    }

    public StudyReviewCountVO reviewCounts(long userId) {
        return jdbc.queryForObject("SELECT "
                        + "COALESCE(SUM(CASE WHEN review_count=1 THEN 1 ELSE 0 END),0) AS once_count,"
                        + "COALESCE(SUM(CASE WHEN review_count=2 THEN 1 ELSE 0 END),0) AS twice_count,"
                        + "COALESCE(SUM(CASE WHEN review_count BETWEEN 3 AND 5 THEN 1 ELSE 0 END),0) AS middle_count,"
                        + "COALESCE(SUM(CASE WHEN review_count>=6 THEN 1 ELSE 0 END),0) AS high_count "
                        + "FROM word_learning_progress WHERE user_id=? AND card_type='EN_ZH'",
                (rs, rowNum) -> new StudyReviewCountVO(rs.getInt("once_count"), rs.getInt("twice_count"),
                        rs.getInt("middle_count"), rs.getInt("high_count")), userId);
    }

    public int countDueAttempts(long userId, Instant start, Instant end) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM word_review_record "
                + "WHERE user_id=? AND reviewed_at>=? AND reviewed_at<? AND due_before IS NOT NULL "
                + "AND due_before<=reviewed_at AND (SELECT previous_review.state_after "
                + "FROM word_review_record previous_review "
                + "WHERE previous_review.progress_id=word_review_record.progress_id "
                + "AND previous_review.id<word_review_record.id "
                + "ORDER BY previous_review.id DESC LIMIT 1)='REVIEW'",
                Integer.class, userId, dbTime(start), dbTime(end));
    }

    public int countDueSuccesses(long userId, Instant start, Instant end) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM word_review_record "
                + "WHERE user_id=? AND reviewed_at>=? AND reviewed_at<? AND due_before IS NOT NULL "
                + "AND due_before<=reviewed_at AND rating>1 AND (SELECT previous_review.state_after "
                + "FROM word_review_record previous_review "
                + "WHERE previous_review.progress_id=word_review_record.progress_id "
                + "AND previous_review.id<word_review_record.id "
                + "ORDER BY previous_review.id DESC LIMIT 1)='REVIEW'",
                Integer.class,
                userId, dbTime(start), dbTime(end));
    }

    public boolean accessibleBook(long userId, long bookId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM word_book WHERE id=? AND is_deleted=0 "
                + "AND (book_type='SYSTEM' OR user_id=?)", Integer.class, bookId, userId);
        return count != null && count > 0;
    }

    public void setBookEnabled(long userId, long bookId, boolean enabled) {
        jdbc.update("INSERT INTO user_study_book (user_id,book_id,enabled) VALUES (?,?,?) "
                + "ON DUPLICATE KEY UPDATE enabled=VALUES(enabled)", userId, bookId, enabled);
    }

    public boolean bookEnabled(long userId, long bookId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM user_study_book "
                + "WHERE user_id=? AND book_id=? AND enabled=1", Integer.class, userId, bookId);
        return count != null && count > 0;
    }

    public int bookLearnedCount(long userId, long bookId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM word_book_dictionary_relation r "
                + "JOIN word_learning_progress p ON p.word_id=r.word_id AND p.user_id=? AND p.card_type='EN_ZH' "
                + "WHERE r.word_book_id=?", Integer.class, userId, bookId);
    }
}
