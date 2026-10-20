package com.hope.xueling.english.vocab.service.impl;

import com.hope.xueling.english.vocab.service.ReviewScheduler;
import io.github.openspacedrepetition.Card;
import io.github.openspacedrepetition.Rating;
import io.github.openspacedrepetition.Scheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class FsrsReviewScheduler implements ReviewScheduler {
    private static final Rating[] RATINGS = {Rating.AGAIN, Rating.HARD, Rating.GOOD, Rating.EASY};

    @Override
    public String version() {
        return "java-fsrs-1.0.0-no-fuzz";
    }

    @Override
    public ScheduledReview review(String previousCardJson, int rating, double desiredRetention, Instant reviewedAt) {
        Card card = previousCardJson == null
                ? Card.builder().due(reviewedAt).build()
                : Card.fromJson(previousCardJson);
        Scheduler scheduler = Scheduler.builder()
                .desiredRetention(desiredRetention)
                .enableFuzzing(false)
                .build();
        Card next = scheduler.reviewCard(card, RATINGS[rating - 1], reviewedAt).card();
        return new ScheduledReview(next.toJson(), next.getState().name(), next.getDue());
    }
}
