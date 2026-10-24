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
import com.hope.xueling.english.vocab.domain.vo.StudyCalendarVO;
import java.util.List;

/**
 * 学习计划服务接口：今日队列、评分提交、统计、日历与学习设置，全部以用户为维度隔离数据
 * @author 谢光湘
 * @since 2026/10/24
 */
public interface IStudyService {
    /**
     * 获取今日学习队列：到期复习卡优先，再按每日新词上限补充新词
     * @param userId 用户 ID
     * @return 今日学习数据
     */
    TodayStudyVO today(long userId);
    /**
     * 提交一次四档评分，按 FSRS 计算下次到期时间，需通过请求幂等与版本校验
     * @param userId 用户 ID
     * @param request 评分请求，包含单词 ID、评分、版本与请求编号
     * @return 评分后的排程结果
     */
    StudyReviewResultVO submitReview(long userId, SubmitStudyReviewDTO request);
    /**
     * 获取学习统计概览
     * @param userId 用户 ID
     * @return 学习统计
     */
    StudyStatsVO stats(long userId);
    /**
     * 获取最近若干天的学习记录
     * @param userId 用户 ID
     * @param days 统计天数
     * @return 按天排列的学习记录
     */
    List<StudyDayStatsVO> history(long userId, int days);
    /**
     * 获取学习日历与连续打卡统计
     * @param userId 用户 ID
     * @param month 月份，格式 yyyy-MM，为空时取当前月
     * @return 日历数据
     */
    StudyCalendarVO calendar(long userId, String month);
    /**
     * 按复习次数分档统计词数
     * @param userId 用户 ID
     * @return 复习次数分布
     */
    StudyReviewCountVO reviewCounts(long userId);
    /**
     * 查询词条熟悉度分布与分页明细，可按词书与掌握阶段过滤
     * @param userId 用户 ID
     * @param bookId 词书 ID，为空表示全部词书
     * @param stage 掌握阶段：ALL、LEARNING、CONSOLIDATING、STABLE
     * @param page 页码，从 1 开始
     * @return 熟悉度分布与分页明细
     */
    StudyFamiliarityVO familiarity(long userId, Long bookId, String stage, int page);
    /**
     * 获取学习设置，用户尚未设置时返回默认值
     * @param userId 用户 ID
     * @return 学习设置
     */
    StudySettingsVO settings(long userId);
    /**
     * 更新学习设置
     * @param userId 用户 ID
     * @param request 时区、每日新词上限与目标记忆率
     * @return 更新后的学习设置
     */
    StudySettingsVO updateSettings(long userId, UpdateStudySettingsDTO request);
    /**
     * 启用或停用某个词书参与学习计划
     * @param userId 用户 ID
     * @param bookId 词书 ID
     * @param enabled 是否纳入学习计划
     */
    void setBookEnabled(long userId, long bookId, boolean enabled);
}
