package com.campus.lostfound.dao;

import com.campus.lostfound.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物品数据访问层接口
 */
@Mapper
public interface ItemMapper {

    /**
     * 根据ID查询物品（含用户和分类关联信息）
     * @param id 物品ID
     * @return 物品实体
     */
    Item selectById(@Param("id") Long id);

    /**
     * 多条件分页查询物品列表
     * @param categoryId 分类ID
     * @param itemType 物品类型
     * @param status 状态
     * @param keyword 关键词
     * @param location 地点
     * @return 物品列表
     */
    List<Item> selectList(@Param("categoryId") Long categoryId,
                          @Param("itemType") Integer itemType,
                          @Param("status") Integer status,
                          @Param("keyword") String keyword,
                          @Param("location") String location);

    /**
     * 根据用户ID查询物品列表
     * @param userId 用户ID
     * @return 物品列表
     */
    List<Item> selectByUserId(@Param("userId") Long userId);

    /**
     * 新增物品
     * @param item 物品实体
     * @return 影响行数
     */
    int insert(Item item);

    /**
     * 更新物品
     * @param item 物品实体
     * @return 影响行数
     */
    int updateById(Item item);

    /**
     * 删除物品
     * @param id 物品ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
