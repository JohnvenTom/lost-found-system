package com.campus.lostfound.service;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.common.PageResult;
import com.campus.lostfound.dao.ItemMapper;
import com.campus.lostfound.entity.Category;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.entity.dto.ItemDTO;
import com.campus.lostfound.exception.BusinessException;
import com.campus.lostfound.service.ai.AiClassificationService;
import com.campus.lostfound.service.ai.AiContentModerationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 物品服务层，处理失物信息的发布、查找、审核等业务逻辑
 * 集成AI智能分类和内容安全审核
 */
@Slf4j
@Service
public class ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AiClassificationService aiClassificationService;

    @Autowired
    private AiContentModerationService aiContentModerationService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 发布失物信息（含AI分类和内容审核）
     * @param dto 物品信息
     * @param userId 发布用户ID
     * @return 发布成功的物品信息
     * @throws BusinessException 内容审核不通过时抛出
     */
    @Transactional
    public Item publishItem(ItemDTO dto, Long userId) {
        AiContentModerationService.ModerationResult moderationResult =
                aiContentModerationService.moderate(dto.getTitle(), dto.getDescription());

        if (!moderationResult.isPass()) {
            throw new BusinessException("内容审核未通过: " + moderationResult.getReason());
        }

        String aiCategoryName = aiClassificationService.classify(dto.getTitle(), dto.getDescription());
        Category category = categoryService.getCategoryByName(aiCategoryName);

        Item item = new Item();
        item.setUserId(userId);
        item.setCategoryId(category != null ? category.getId() : null);
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setImages(dto.getImages());
        item.setLocation(dto.getLocation());
        item.setContactInfo(dto.getContactInfo());
        item.setItemType(dto.getItemType() != null ? dto.getItemType() : 0);
        item.setStatus(Constants.ITEM_STATUS_APPROVED);
        item.setAiCategory(aiCategoryName);
        item.setAiReviewPass(Constants.CONTENT_REVIEW_PASS);
        item.setAiReviewResult(moderationResult.getReason());
        item.setFoundTime(dto.getFoundTime() != null ? LocalDateTime.parse(dto.getFoundTime(), FORMATTER) : null);

        itemMapper.insert(item);
        log.info("物品发布成功: id={}, title={}, aiCategory={}", item.getId(), item.getTitle(), aiCategoryName);
        return itemMapper.selectById(item.getId());
    }

    /**
     * 多条件分页查询物品列表
     * @param categoryId 分类ID
     * @param itemType 物品类型
     * @param status 状态
     * @param keyword 关键词
     * @param location 地点
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResult<Item> getItemList(Long categoryId, Integer itemType, Integer status,
                                         String keyword, String location, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Item> items = itemMapper.selectList(categoryId, itemType, status, keyword, location);
        long total = ((Page<?>) items).getTotal();
        int pageNumResult = ((Page<?>) items).getPageNum();
        int pageSizeResult = ((Page<?>) items).getPageSize();
        return PageResult.of(total, pageNumResult, pageSizeResult, items);
    }

    /**
     * 根据ID查询物品详情
     * @param id 物品ID
     * @return 物品详情
     */
    public Item getItemById(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        return item;
    }

    /**
     * 根据用户ID查询物品列表
     * @param userId 用户ID
     * @return 物品列表
     */
    public List<Item> getItemsByUserId(Long userId) {
        return itemMapper.selectByUserId(userId);
    }

    /**
     * 更新物品信息
     * @param item 物品实体
     * @return 更新后的物品
     */
    public Item updateItem(Item item) {
        itemMapper.updateById(item);
        log.info("物品更新成功: id={}", item.getId());
        return itemMapper.selectById(item.getId());
    }

    /**
     * 审核物品（管理员操作）
     * @param id 物品ID
     * @param status 审核状态
     * @param currentUserRole 当前操作用户角色
     */
    public void reviewItem(Long id, Integer status, Integer currentUserRole) {
        if (!Constants.ROLE_ADMIN.equals(currentUserRole)) {
            throw new BusinessException("无权限操作，仅管理员可审核");
        }
        Item item = new Item();
        item.setId(id);
        item.setStatus(status);
        itemMapper.updateById(item);
        log.info("物品审核完成: id={}, status={}", id, status);
    }

    /**
     * 删除物品
     * @param id 物品ID
     * @param userId 操作用户ID
     * @param role 操作用户角色
     */
    public void deleteItem(Long id, Long userId, Integer role) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (!Constants.ROLE_ADMIN.equals(role) && !item.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该物品");
        }
        itemMapper.deleteById(id);
        log.info("物品删除成功: id={}", id);
    }
}
