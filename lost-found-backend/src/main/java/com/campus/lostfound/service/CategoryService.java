package com.campus.lostfound.service;

import com.campus.lostfound.dao.CategoryMapper;
import com.campus.lostfound.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务层，处理物品分类的CRUD操作
 */
@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取所有正常状态的分类列表
     * @return 分类列表
     */
    public List<Category> getAllCategories() {
        return categoryMapper.selectAll();
    }

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类实体
     */
    public Category getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }

    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类实体
     */
    public Category getCategoryByName(String name) {
        return categoryMapper.selectByName(name);
    }

    /**
     * 新增分类
     * @param category 分类实体
     * @return 新增后的分类
     */
    public Category addCategory(Category category) {
        category.setStatus(1);
        categoryMapper.insert(category);
        log.info("新增分类: {}", category.getName());
        return category;
    }

    /**
     * 更新分类
     * @param category 分类实体
     * @return 更新后的分类
     */
    public Category updateCategory(Category category) {
        categoryMapper.updateById(category);
        log.info("更新分类: id={}", category.getId());
        return categoryMapper.selectById(category.getId());
    }

    /**
     * 删除分类
     * @param id 分类ID
     */
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
        log.info("删除分类: id={}", id);
    }
}
