package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Message;
import com.campus.lostfound.entity.dto.MessageDTO;
import com.campus.lostfound.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 留言控制器，提供物品留言互动接口
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发布留言
     * @param dto 留言信息
     * @param request HTTP请求
     * @return 留言记录
     */
    @PostMapping("/add")
    public Result<Message> addMessage(@Validated @RequestBody MessageDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Message message = messageService.addMessage(dto, userId);
        return Result.success(message);
    }

    /**
     * 查询物品的留言列表
     * @param itemId 物品ID
     * @return 留言列表
     */
    @GetMapping("/list/{itemId}")
    public Result<List<Message>> getMessagesByItem(@PathVariable Long itemId) {
        List<Message> messages = messageService.getMessagesByItemId(itemId);
        return Result.success(messages);
    }

    /**
     * 删除留言
     * @param id 留言ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Integer role = (Integer) request.getAttribute("currentUserRole");
        messageService.deleteMessage(id, userId, role);
        return Result.success();
    }
}
