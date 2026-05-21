package com.campus.lostfound.controller;

import com.campus.lostfound.common.PageResult;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.entity.dto.ItemDTO;
import com.campus.lostfound.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 物品控制器，提供失物信息发布、查找、审核等接口
 */
@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 发布失物信息（含AI分类和内容审核）
     * @param dto 物品信息
     * @param request HTTP请求
     * @return 发布成功的物品信息
     */
    @PostMapping("/publish")
    public Result<Item> publishItem(@Validated @RequestBody ItemDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Item item = itemService.publishItem(dto, userId);
        return Result.success("发布成功", item);
    }

    /**
     * 多条件分页查询物品列表
     * @param categoryId 分类ID
     * @param itemType 物品类型（0失物/1寻物）
     * @param status 状态（0待审核/1已通过/2已拒绝/3已认领）
     * @param keyword 关键词
     * @param location 地点
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResult<Item>> getItemList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer itemType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        if (status == null) {
            status = 1;
        }
        PageResult<Item> pageResult = itemService.getItemList(categoryId, itemType, status, keyword, location, pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 查询物品详情
     * @param id 物品ID
     * @return 物品详情
     */
    @GetMapping("/detail/{id}")
    public Result<Item> getItemDetail(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        return Result.success(item);
    }

    /**
     * 查询当前用户发布的物品
     * @param request HTTP请求
     * @return 物品列表
     */
    @GetMapping("/my")
    public Result<List<Item>> getMyItems(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<Item> items = itemService.getItemsByUserId(userId);
        return Result.success(items);
    }

    /**
     * 更新物品信息
     * @param id 物品ID
     * @param item 物品实体
     * @param request HTTP请求
     * @return 更新后的物品
     */
    @PutMapping("/update/{id}")
    public Result<Item> updateItem(@PathVariable Long id, @RequestBody Item item, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Integer role = (Integer) request.getAttribute("currentUserRole");
        Item existing = itemService.getItemById(id);
        if (role != 1 && !existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限修改该物品");
        }
        item.setId(id);
        Item updated = itemService.updateItem(item);
        return Result.success(updated);
    }

    /**
     * 管理员：审核物品
     * @param id 物品ID
     * @param status 审核状态
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/review/{id}")
    public Result<Void> reviewItem(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("currentUserRole");
        itemService.reviewItem(id, status, role);
        return Result.success();
    }

    /**
     * 删除物品
     * @param id 物品ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteItem(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Integer role = (Integer) request.getAttribute("currentUserRole");
        itemService.deleteItem(id, userId, role);
        return Result.success();
    }
}
