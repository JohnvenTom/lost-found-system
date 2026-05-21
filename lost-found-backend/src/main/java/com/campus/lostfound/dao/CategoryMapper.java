package com.campus.lostfound.dao;

import com.campus.lostfound.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类数据访问层接口
 */
@Mapper
public interface CategoryMapper {

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类实体
     */
    Category selectById(@Param("id") Long id);

    /**
     * 查询所有正常状态的分类
     * @return 分类列表
     */
    List<Category> selectAll();

    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类实体
     */
    Category selectByName(@Param("name") String name);

    /**
     * 新增分类
     * @param category 分类实体
     * @return 影响行数
     */
    int insert(Category category);

    /**
     * 更新分类
     * @param category 分类实体
     * @return 影响行数
     */
    int updateById(Category category);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
