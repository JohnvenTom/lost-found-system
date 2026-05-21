package com.campus.lostfound.dao;

import com.campus.lostfound.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 留言数据访问层接口
 */
@Mapper
public interface MessageMapper {

    /**
     * 根据ID查询留言
     * @param id 留言ID
     * @return 留言实体
     */
    Message selectById(@Param("id") Long id);

    /**
     * 根据物品ID查询留言列表
     * @param itemId 物品ID
     * @return 留言列表
     */
    List<Message> selectByItemId(@Param("itemId") Long itemId);

    /**
     * 新增留言
     * @param message 留言实体
     * @return 影响行数
     */
    int insert(Message message);

    /**
     * 删除留言
     * @param id 留言ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
