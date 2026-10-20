package com.hope.xueling.english.vocab.service;

import java.time.Instant;

public interface ReviewScheduler {
    String version();

    ScheduledReview review(String previousCardJson, int rating, double desiredRetention, Instant reviewedAt);

    record ScheduledReview(String cardJson, String state, Instant dueAt) {}
}
