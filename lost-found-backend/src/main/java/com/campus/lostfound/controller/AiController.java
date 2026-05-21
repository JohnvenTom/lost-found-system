package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.service.ai.AiClassificationService;
import com.campus.lostfound.service.ai.AiContentModerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI功能控制器，提供智能分类和内容审核的独立调用接口
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiClassificationService aiClassificationService;

    @Autowired
    private AiContentModerationService aiContentModerationService;

    /**
     * AI智能分类接口
     * @param title 物品名称
     * @param description 物品描述
     * @return 分类结果
     */
    @GetMapping("/classify")
    public Result<Map<String, String>> classify(@RequestParam String title,
                                                 @RequestParam String description) {
        String category = aiClassificationService.classify(title, description);
        Map<String, String> data = new HashMap<>();
        data.put("category", category);
        return Result.success(data);
    }

    /**
     * AI内容安全审核接口
     * @param title 内容标题
     * @param description 内容描述
     * @return 审核结果
     */
    @GetMapping("/moderate")
    public Result<Map<String, Object>> moderate(@RequestParam String title,
                                                 @RequestParam String description) {
        AiContentModerationService.ModerationResult result = aiContentModerationService.moderate(title, description);
        Map<String, Object> data = new HashMap<>();
        data.put("pass", result.isPass());
        data.put("reason", result.getReason());
        return Result.success(data);
    }
}
