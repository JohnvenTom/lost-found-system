package com.campus.lostfound.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * AI内容安全审核服务，基于Spring AI实现自动内容审核
 * 检测违规、敏感、垃圾信息，调用失败时降级为关键词过滤
 */
@Slf4j
@Service
public class AiContentModerationService {

    @Autowired
    private ChatModel chatModel;

    private static final String MODERATION_PROMPT =
            "你是一个内容安全审核助手。请审核以下内容是否包含违规、敏感、色情、暴力、诈骗、垃圾广告等不良信息。\n\n" +
            "审核内容：\n" +
            "标题：{title}\n" +
            "描述：{description}\n\n" +
            "请按以下格式回复（严格遵守）：\n" +
            "通过/拒绝|审核说明\n\n" +
            "示例：\n" +
            "通过|内容正常，无违规信息\n" +
            "拒绝|包含虚假诈骗信息";

    /**
     * 审核内容安全性
     * @param title 内容标题
     * @param description 内容描述
     * @return 审核结果对象
     */
    public ModerationResult moderate(String title, String description) {
        try {
            log.info("开始AI内容审核 - 标题: {}, 描述: {}", title, description);
            PromptTemplate promptTemplate = new PromptTemplate(MODERATION_PROMPT);
            Map<String, Object> variables = new HashMap<>();
            variables.put("title", title);
            variables.put("description", description);

            Prompt prompt = promptTemplate.create(variables);
            log.info("发送AI审核请求 - 提示词: {}", prompt.getContents());
            
            ChatResponse response = chatModel.call(prompt);
            String result = response.getResult().getOutput().getContent().trim();

            log.info("AI内容审核结果 - 原始响应: {}", result);

            ModerationResult moderationResult = parseModerationResult(result);
            log.info("AI内容审核最终结果 - 通过: {}, 原因: {}", moderationResult.isPass(), moderationResult.getReason());
            
            return moderationResult;
        } catch (Exception e) {
            log.error("AI内容审核服务调用失败，降级为关键词过滤", e);
            return fallbackModerate(title, description);
        }
    }

    /**
     * 解析AI审核结果
     * @param result AI返回的审核结果字符串
     * @return 审核结果对象
     */
    private ModerationResult parseModerationResult(String result) {
        ModerationResult moderationResult = new ModerationResult();
        try {
            String[] parts = result.split("\\|");
            if (parts.length >= 1) {
                String decision = parts[0].trim();
                moderationResult.setPass("通过".equals(decision));
            }
            if (parts.length >= 2) {
                moderationResult.setReason(parts[1].trim());
            } else {
                moderationResult.setReason(moderationResult.isPass() ? "内容安全，通过审核" : "内容可能包含违规信息");
            }
        } catch (Exception e) {
            log.warn("解析AI审核结果异常: {}", e.getMessage());
            moderationResult.setPass(true);
            moderationResult.setReason("审核结果解析异常，默认通过");
        }
        return moderationResult;
    }

    /**
     * 降级审核方法：基于关键词过滤
     * @param title 内容标题
     * @param description 内容描述
     * @return 审核结果对象
     */
    private ModerationResult fallbackModerate(String title, String description) {
        ModerationResult result = new ModerationResult();
        String text = (title + " " + description).toLowerCase();

        String[] sensitiveWords = {"赌博", "色情", "毒品", "枪支", "代开发票", "办证",
                "贷款", "兼职刷单", "虚假中奖", "传销", "邪教"};

        for (String word : sensitiveWords) {
            if (text.contains(word)) {
                result.setPass(false);
                result.setReason("内容包含敏感词汇，请修改后重新发布");
                return result;
            }
        }

        result.setPass(true);
        result.setReason("内容安全，通过审核（关键词过滤）");
        return result;
    }

    /**
     * 内容审核结果封装
     */
    public static class ModerationResult {
        private boolean isPass;
        private String reason;

        public boolean isPass() {
            return isPass;
        }

        public void setPass(boolean pass) {
            isPass = pass;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
