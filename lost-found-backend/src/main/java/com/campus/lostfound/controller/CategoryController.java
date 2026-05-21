package com.campus.lostfound.controller;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Category;
import com.campus.lostfound.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类控制器，提供物品分类的查询和管理接口
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/detail/{id}")
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 管理员：新增分类
     * @param category 分类实体
     * @param request HTTP请求
     * @return 新增的分类
     */
    @PostMapping("/add")
    public Result<Category> addCategory(@RequestBody Category category, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("currentUserRole");
        if (!Constants.ROLE_ADMIN.equals(role)) {
            return Result.error(403, "无权限操作，仅管理员可管理分类");
        }
        Category added = categoryService.addCategory(category);
        return Result.success(added);
    }

    /**
     * 管理员：更新分类
     * @param id 分类ID
     * @param category 分类实体
     * @param request HTTP请求
     * @return 更新后的分类
     */
    @PutMapping("/update/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("currentUserRole");
        if (!Constants.ROLE_ADMIN.equals(role)) {
            return Result.error(403, "无权限操作，仅管理员可管理分类");
        }
        category.setId(id);
        Category updated = categoryService.updateCategory(category);
        return Result.success(updated);
    }

    /**
     * 管理员：删除分类
     * @param id 分类ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("currentUserRole");
        if (!Constants.ROLE_ADMIN.equals(role)) {
            return Result.error(403, "无权限操作，仅管理员可管理分类");
        }
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
