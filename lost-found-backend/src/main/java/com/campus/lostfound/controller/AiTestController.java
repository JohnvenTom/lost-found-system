package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.service.ai.AiClassificationService;
import com.campus.lostfound.service.ai.AiContentModerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI测试控制器 - 用于调试和测试AI功能
 */
@Slf4j
@RestController
@RequestMapping("/ai-test")
public class AiTestController {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private AiClassificationService aiClassificationService;

    @Autowired
    private AiContentModerationService aiContentModerationService;

    /**
     * 简单的AI对话测试
     */
    @GetMapping("/simple-chat")
    public Result<Map<String, Object>> simpleChat(@RequestParam String message) {
        try {
            log.info("开始简单AI对话测试 - 消息: {}", message);
            Prompt prompt = new Prompt(message);
            ChatResponse response = chatModel.call(prompt);
            String content = response.getResult().getOutput().getContent();
            
            Map<String, Object> result = new HashMap<>();
            result.put("request", message);
            result.put("response", content);
            result.put("success", true);
            
            log.info("简单AI对话测试成功 - 响应: {}", content);
            return Result.success(result);
        } catch (Exception e) {
            log.error("简单AI对话测试失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("request", message);
            result.put("error", e.getMessage());
            result.put("success", false);
            return Result.success("AI调用失败: " + e.getMessage(), result);
        }
    }

    /**
     * 测试分类功能
     */
    @GetMapping("/test-classify")
    public Result<Map<String, Object>> testClassify(@RequestParam String title,
                                                    @RequestParam String description) {
        try {
            log.info("开始AI分类测试");
            String category = aiClassificationService.classify(title, description);
            
            Map<String, Object> result = new HashMap<>();
            result.put("title", title);
            result.put("description", description);
            result.put("category", category);
            result.put("success", true);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("AI分类测试失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("error", e.getMessage());
            result.put("success", false);
            return Result.success("分类测试失败: " + e.getMessage(), result);
        }
    }

    /**
     * 测试内容审核功能
     */
    @GetMapping("/test-moderate")
    public Result<Map<String, Object>> testModerate(@RequestParam String title,
                                                    @RequestParam String description) {
        try {
            log.info("开始AI内容审核测试");
            AiContentModerationService.ModerationResult moderationResult = 
                    aiContentModerationService.moderate(title, description);
            
            Map<String, Object> result = new HashMap<>();
            result.put("title", title);
            result.put("description", description);
            result.put("pass", moderationResult.isPass());
            result.put("reason", moderationResult.getReason());
            result.put("success", true);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("AI内容审核测试失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("error", e.getMessage());
            result.put("success", false);
            return Result.success("审核测试失败: " + e.getMessage(), result);
        }
    }
}
