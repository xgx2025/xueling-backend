package com.hope.xueling.english.reading.service.impl;

import com.hope.xueling.common.exception.ForbiddenException;
import com.hope.xueling.common.exception.ValidationException;
import com.hope.xueling.common.mapper.UserMapper;
import com.hope.xueling.english.reading.domain.entity.Article;
import com.hope.xueling.english.reading.mapper.ArticleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ArticleServiceImplTest {
    private Boolean isFree;
    private Long vipUserId;
    private String phraseSummary;
    private Article article;
    private AtomicInteger bodyReads;
    private ArticleServiceImpl service;

    @BeforeEach
    void setUp() {
        bodyReads = new AtomicInteger();
        ArticleMapper articleMapper = (ArticleMapper) Proxy.newProxyInstance(
                ArticleMapper.class.getClassLoader(), new Class<?>[]{ArticleMapper.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "selectPublishedIsFree" -> isFree;
                    case "selectArticlePhrases" -> phraseSummary;
                    case "selectPublishedArticleById" -> {
                        bodyReads.incrementAndGet();
                        yield article;
                    }
                    default -> throw new AssertionError("Unexpected article mapper call: " + method.getName());
                });
        UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(
                UserMapper.class.getClassLoader(), new Class<?>[]{UserMapper.class},
                (proxy, method, args) -> {
                    if ("isVip".equals(method.getName())) return vipUserId;
                    throw new AssertionError("Unexpected user mapper call: " + method.getName());
                });
        service = new ArticleServiceImpl(articleMapper, userMapper, null, null, null, null);
    }

    @Test
    void freeArticleCanBeReadWithoutMembership() {
        isFree = true;
        article = new Article();
        article.setId(10L);
        assertSame(article, service.getArticleById(7L, 10L));
        assertEquals(1, bodyReads.get());
    }

    @Test
    void paidArticleRequiresValidMembershipBeforeLoadingBody() {
        isFree = false;
        assertThrows(ForbiddenException.class, () -> service.getArticleById(7L, 10L));
        assertEquals(0, bodyReads.get());

        vipUserId = 7L;
        article = new Article();
        article.setId(10L);
        assertSame(article, service.getArticleById(7L, 10L));
        assertEquals(1, bodyReads.get());
    }

    @Test
    void draftAndOfflineArticlesAreNotReadable() {
        assertThrows(ValidationException.class, () -> service.getArticleById(7L, 10L));
        assertEquals(0, bodyReads.get());
    }

    @Test
    void cachedPhraseSummaryIsReturnedForMember() {
        isFree = true;
        vipUserId = 7L;
        phraseSummary = "已有词汇总结";
        assertEquals(phraseSummary, service.summarizeEnglishPhrases(7L, 10L));
        assertEquals(0, bodyReads.get());
    }
}
