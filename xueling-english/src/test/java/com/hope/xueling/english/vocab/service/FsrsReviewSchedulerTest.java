package com.hope.xueling.english.vocab.service;

import com.hope.xueling.english.vocab.service.impl.FsrsReviewScheduler;
import io.github.openspacedrepetition.Card;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FsrsReviewSchedulerTest {
    private final ReviewScheduler scheduler = new FsrsReviewScheduler();

    @Test
    void sameRatingsAndTimesProduceSameDueDatesAfterSerialization() {
        Instant first = Instant.parse("2026-10-20T02:00:00Z");
        Instant second = first.plusSeconds(900);
        var leftFirst = scheduler.review(null, 3, 0.9, first);
        var rightFirst = scheduler.review(null, 3, 0.9, first);
        assertEquals(leftFirst.dueAt(), rightFirst.dueAt());
        assertEquals(leftFirst.state(), rightFirst.state());

        var leftSecond = scheduler.review(leftFirst.cardJson(), 2, 0.9, second);
        var rightSecond = scheduler.review(rightFirst.cardJson(), 2, 0.9, second);
        assertEquals(leftSecond.dueAt(), rightSecond.dueAt());
        assertEquals(leftSecond.state(), rightSecond.state());
        assertEquals(leftSecond.dueAt(), Card.fromJson(leftSecond.cardJson()).getDue());
    }

    @Test
    void forgettingAndRememberingHaveDifferentSchedules() {
        Instant at = Instant.parse("2026-10-20T02:00:00Z");
        var forgot = scheduler.review(null, 1, 0.9, at);
        var remembered = scheduler.review(null, 4, 0.9, at);
        assertTrue(forgot.dueAt().isBefore(remembered.dueAt()));
    }
}
