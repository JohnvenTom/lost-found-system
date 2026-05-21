package com.campus.lostfound.service;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.dao.ClaimMapper;
import com.campus.lostfound.dao.ItemMapper;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.entity.dto.ClaimDTO;
import com.campus.lostfound.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 认领服务层，处理失物认领申请和审核流程
 */
@Slf4j
@Service
public class ClaimService {

    @Autowired
    private ClaimMapper claimMapper;

    @Autowired
    private ItemMapper itemMapper;

    /**
     * 提交认领申请
     * @param dto 认领信息
     * @param userId 认领用户ID
     * @return 认领记录
     * @throws BusinessException 物品不存在或不能认领时抛出
     */
    @Transactional
    public Claim submitClaim(ClaimDTO dto, Long userId) {
        Item item = itemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        if (item.getStatus().equals(Constants.ITEM_STATUS_CLAIMED)) {
            throw new BusinessException("该物品已被认领");
        }
        if (item.getUserId().equals(userId)) {
            throw new BusinessException("不能认领自己发布的物品");
        }

        Claim claim = new Claim();
        claim.setItemId(dto.getItemId());
        claim.setUserId(userId);
        claim.setReason(dto.getReason());
        claim.setContactInfo(dto.getContactInfo());
        claim.setStatus(Constants.CLAIM_STATUS_PENDING);

        claimMapper.insert(claim);
        log.info("认领申请提交成功: itemId={}, userId={}", dto.getItemId(), userId);
        return claimMapper.selectById(claim.getId());
    }

    /**
     * 根据物品ID查询认领列表
     * @param itemId 物品ID
     * @return 认领列表
     */
    public List<Claim> getClaimsByItemId(Long itemId) {
        return claimMapper.selectByItemId(itemId);
    }

    /**
     * 根据用户ID查询认领列表
     * @param userId 用户ID
     * @return 认领列表
     */
    public List<Claim> getClaimsByUserId(Long userId) {
        return claimMapper.selectByUserId(userId);
    }

    /**
     * 审核认领申请
     * @param claimId 认领ID
     * @param status 审核状态
     * @param reviewRemark 审核备注
     * @param currentUserRole 当前操作用户角色
     * @param currentUserId 当前操作用户ID
     */
    @Transactional
    public void reviewClaim(Long claimId, Integer status, String reviewRemark,
                            Integer currentUserRole, Long currentUserId) {
        Claim claim = claimMapper.selectById(claimId);
        if (claim == null) {
            throw new BusinessException("认领记录不存在");
        }

        Item item = itemMapper.selectById(claim.getItemId());
        if (!Constants.ROLE_ADMIN.equals(currentUserRole) && !item.getUserId().equals(currentUserId)) {
            throw new BusinessException("无权限审核该认领申请");
        }

        claim.setStatus(status);
        claim.setReviewRemark(reviewRemark);
        claimMapper.updateById(claim);

        if (Constants.CLAIM_STATUS_APPROVED.equals(status)) {
            Item updateItem = new Item();
            updateItem.setId(claim.getItemId());
            updateItem.setStatus(Constants.ITEM_STATUS_CLAIMED);
            itemMapper.updateById(updateItem);
        }

        log.info("认领审核完成: claimId={}, status={}", claimId, status);
    }

    /**
     * 删除认领记录
     * @param id 认领ID
     * @param userId 操作用户ID
     * @param role 操作用户角色
     */
    public void deleteClaim(Long id, Long userId, Integer role) {
        Claim claim = claimMapper.selectById(id);
        if (claim == null) {
            throw new BusinessException("认领记录不存在");
        }
        if (!Constants.ROLE_ADMIN.equals(role) && !claim.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该认领记录");
        }
        claimMapper.deleteById(id);
        log.info("认领记录删除: id={}", id);
    }
}
