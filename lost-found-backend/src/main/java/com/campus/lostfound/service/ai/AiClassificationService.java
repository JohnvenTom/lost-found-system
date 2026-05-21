package com.campus.lostfound.service.ai;

import com.campus.lostfound.dao.CategoryMapper;
import com.campus.lostfound.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI智能分类服务，基于Spring AI实现物品自动分类
 * 调用失败时降级为关键词匹配分类，不影响主流程
 */
@Slf4j
@Service
public class AiClassificationService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private CategoryMapper categoryMapper;

    private static final String CLASSIFICATION_PROMPT =
            "你是一个物品分类助手。请根据以下物品信息，从给定的分类列表中选择最合适的一个分类。\n\n" +
            "可选分类列表：{categories}\n\n" +
            "物品名称：{title}\n" +
            "物品描述：{description}\n\n" +
            "请只返回分类名称，不要返回其他任何内容。如果无法确定分类，请返回\"其他物品\"。";

    /**
     * 对物品进行智能分类
     * @param title 物品名称
     * @param description 物品描述
     * @return 分类名称
     */
    public String classify(String title, String description) {
        try {
            log.info("开始AI分类 - 物品: {}, 描述: {}", title, description);
            List<Category> categories = categoryMapper.selectAll();
            String categoryNames = categories.stream()
                    .map(Category::getName)
                    .collect(Collectors.joining("、"));
            log.info("可用分类: {}", categoryNames);

            PromptTemplate promptTemplate = new PromptTemplate(CLASSIFICATION_PROMPT);
            Map<String, Object> variables = new HashMap<>();
            variables.put("categories", categoryNames);
            variables.put("title", title);
            variables.put("description", description);

            Prompt prompt = promptTemplate.create(variables);
            log.info("发送AI请求 - 提示词: {}", prompt.getContents());
            
            ChatResponse response = chatModel.call(prompt);
            String result = response.getResult().getOutput().getContent().trim();

            log.info("AI分类结果 - 原始响应: {}, 分类: {}", result, result);

            Category matched = categories.stream()
                    .filter(c -> c.getName().equals(result))
                    .findFirst()
                    .orElse(null);

            if (matched != null) {
                log.info("分类匹配成功: {}", matched.getName());
                return matched.getName();
            }

            for (Category category : categories) {
                if (result.contains(category.getName())) {
                    log.info("分类部分匹配: {}", category.getName());
                    return category.getName();
                }
            }

            log.info("使用默认分类: 其他物品");
            return "其他物品";
        } catch (Exception e) {
            log.error("AI分类服务调用失败，降级为关键词匹配", e);
            return fallbackClassify(title, description);
        }
    }

    /**
     * 降级分类方法：基于关键词匹配
     * @param title 物品名称
     * @param description 物品描述
     * @return 分类名称
     */
    private String fallbackClassify(String title, String description) {
        String text = " " + (title + " " + description).toLowerCase() + " ";

        if (text.contains(" 卡 ") || text.contains("证") || text.contains("身份证") || text.contains("学生证")
                || text.contains("校园卡") || text.contains("银行卡") || text.contains("门禁卡")) {
            return "证件卡类";
        }
        if (text.contains("钥匙") || text.contains("钱包") || text.contains("现金") || text.contains("银行卡")) {
            return "钥匙钱包";
        }
        if (text.contains("手机") || text.contains("电脑") || text.contains("耳机") || text.contains("充电")
                || text.contains("平板") || text.contains("电子") || text.contains("相机") || text.contains("U盘")) {
            return "电子产品";
        }
        if (text.contains("书") || text.contains("笔") || text.contains("文具") || text.contains("笔记本")
                || text.contains("作业本") || text.contains("课本")) {
            return "书籍文具";
        }
        if (text.contains("衣服") || text.contains("裤子") || text.contains("鞋") || text.contains("帽子")
                || text.contains("围巾") || text.contains("配饰") || text.contains("包包")) {
            return "衣物配饰";
        }
        if (text.contains("杯子") || text.contains("水壶") || text.contains("伞") || text.contains("雨伞")
                || text.contains("水杯")) {
            return "水杯雨伞";
        }
        if (text.contains("玩具") || text.contains("玩偶") || text.contains("娃娃") || text.contains("游戏")
                || text.contains("手办") || text.contains("模型")) {
            return "玩具";
        }
        return "其他物品";
    }
}
