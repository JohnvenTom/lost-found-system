package com.campus.lostfound.service;

import com.campus.lostfound.dao.MessageMapper;
import com.campus.lostfound.entity.Message;
import com.campus.lostfound.entity.dto.MessageDTO;
import com.campus.lostfound.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 留言服务层，处理物品留言互动功能
 */
@Slf4j
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 发布留言
     * @param dto 留言信息
     * @param userId 留言用户ID
     * @return 留言记录
     */
    public Message addMessage(MessageDTO dto, Long userId) {
        if (dto.getContent() != null && dto.getContent().length() > 500) {
            throw new BusinessException("留言内容不能超过500个字符");
        }

        Message message = new Message();
        message.setItemId(dto.getItemId());
        message.setUserId(userId);
        message.setContent(dto.getContent());
        message.setParentId(dto.getParentId());

        messageMapper.insert(message);
        log.info("留言发布成功: itemId={}, userId={}", dto.getItemId(), userId);
        return messageMapper.selectById(message.getId());
    }

    /**
     * 根据物品ID查询留言列表
     * @param itemId 物品ID
     * @return 留言列表
     */
    public List<Message> getMessagesByItemId(Long itemId) {
        return messageMapper.selectByItemId(itemId);
    }

    /**
     * 删除留言
     * @param id 留言ID
     * @param userId 操作用户ID
     * @param role 操作用户角色
     */
    public void deleteMessage(Long id, Long userId, Integer role) {
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException("留言不存在");
        }
        if (role != 1 && !message.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该留言");
        }
        messageMapper.deleteById(id);
        log.info("留言删除成功: id={}", id);
    }
}
