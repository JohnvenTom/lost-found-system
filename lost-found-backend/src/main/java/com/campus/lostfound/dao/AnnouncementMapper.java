package com.campus.lostfound.dao;

import com.campus.lostfound.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告数据访问层接口
 */
@Mapper
public interface AnnouncementMapper {

    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告实体
     */
    Announcement selectById(@Param("id") Long id);

    /**
     * 查询公告列表
     * @param status 状态筛选
     * @return 公告列表
     */
    List<Announcement> selectList(@Param("status") Integer status);

    /**
     * 新增公告
     * @param announcement 公告实体
     * @return 影响行数
     */
    int insert(Announcement announcement);

    /**
     * 更新公告
     * @param announcement 公告实体
     * @return 影响行数
     */
    int updateById(Announcement announcement);

    /**
     * 删除公告
     * @param id 公告ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
