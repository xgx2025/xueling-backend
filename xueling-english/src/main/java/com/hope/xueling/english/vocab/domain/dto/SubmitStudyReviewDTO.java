package com.hope.xueling.english.vocab.domain.dto;

import lombok.Data;

/**
 * 提交学习评分入参：一次请求对应一张卡片的一次作答
 * @author 谢光湘
 * @since 2026/10/24
 */
@Data
public class SubmitStudyReviewDTO {
    /**
     * 卡片对应的单词 ID
     */
    private String wordId;
    /**
     * 四档评分：1 忘记、2 模糊、3 记得、4 熟练
     */
    private Integer rating;
    /**
     * 客户端持有的卡片版本号，用于乐观并发校验，版本不一致时拒绝写入
     */
    private Integer expectedVersion;
    /**
     * 客户端生成的请求编号，用于网络重试时的幂等去重
     */
    private String requestId;
    /**
     * 本次作答耗时（毫秒），用于统计近似学习时长
     */
    private Integer elapsedMs;
}
