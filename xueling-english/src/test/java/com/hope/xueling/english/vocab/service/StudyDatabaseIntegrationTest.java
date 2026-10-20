package com.hope.xueling.english.vocab.service;

import com.hope.xueling.english.vocab.domain.dto.SubmitStudyReviewDTO;
import com.hope.xueling.english.vocab.mapper.StudyMapper;
import com.hope.xueling.english.vocab.service.impl.FsrsReviewScheduler;
import com.hope.xueling.english.vocab.service.impl.StudyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "XUELING_STUDY_DB_TEST", matches = "true")
class StudyDatabaseIntegrationTest {
    @Test
    void newCardIsSavedOnceAndAllFixtureRowsAreRolledBack() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                "jdbc:mysql://127.0.0.1:3306/xueling_db?serverTimezone=Asia/Shanghai",
                System.getenv("XUELING_DB_USERNAME"), System.getenv("XUELING_DB_PASSWORD"));
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        StudyServiceImpl service = new StudyServiceImpl(new StudyMapper(jdbc), new FsrsReviewScheduler());
        TransactionTemplate transaction = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        AtomicLong fixtureUserId = new AtomicLong();

        transaction.execute(status -> {
            jdbc.update("INSERT INTO user (password) VALUES (?)", "study-fixture-only");
            long userId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            fixtureUserId.set(userId);

            var today = service.today(userId);
            assertFalse(today.getCards().isEmpty());
            assertTrue(today.getCards().getFirst().isNewCard());
            var card = today.getCards().getFirst();
            SubmitStudyReviewDTO request = new SubmitStudyReviewDTO();
            request.setWordId(card.getWordId());
            request.setRating(3);
            request.setExpectedVersion(0);
            request.setRequestId(UUID.randomUUID().toString());
            request.setElapsedMs(1200);

            var first = service.submitReview(userId, request);
            var retry = service.submitReview(userId, request);
            assertEquals(first.getDueAt(), retry.getDueAt());
            assertEquals(1, first.getVersion());
            assertEquals(1, jdbc.queryForObject(
                    "SELECT COUNT(*) FROM word_review_record WHERE user_id=?", Integer.class, userId));
            assertEquals(1, jdbc.queryForObject(
                    "SELECT COUNT(*) FROM word_learning_progress WHERE user_id=?", Integer.class, userId));
            status.setRollbackOnly();
            return null;
        });

        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM user WHERE id=?",
                Integer.class, fixtureUserId.get()));
    }
}
