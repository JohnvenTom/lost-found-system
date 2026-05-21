package com.campus.lostfound.dao;

import com.campus.lostfound.entity.Claim;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 认领数据访问层接口
 */
@Mapper
public interface ClaimMapper {

    /**
     * 根据ID查询认领记录
     * @param id 认领ID
     * @return 认领实体
     */
    Claim selectById(@Param("id") Long id);

    /**
     * 根据物品ID查询认领列表
     * @param itemId 物品ID
     * @return 认领列表
     */
    List<Claim> selectByItemId(@Param("itemId") Long itemId);

    /**
     * 根据用户ID查询认领列表
     * @param userId 用户ID
     * @return 认领列表
     */
    List<Claim> selectByUserId(@Param("userId") Long userId);

    /**
     * 新增认领申请
     * @param claim 认领实体
     * @return 影响行数
     */
    int insert(Claim claim);

    /**
     * 更新认领记录
     * @param claim 认领实体
     * @return 影响行数
     */
    int updateById(Claim claim);

    /**
     * 删除认领记录
     * @param id 认领ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
