package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.dto.ClaimDTO;
import com.campus.lostfound.service.ClaimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 认领控制器，提供失物认领申请和审核接口
 */
@Slf4j
@RestController
@RequestMapping("/claim")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    /**
     * 提交认领申请
     * @param dto 认领信息
     * @param request HTTP请求
     * @return 认领记录
     */
    @PostMapping("/submit")
    public Result<Claim> submitClaim(@Validated @RequestBody ClaimDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Claim claim = claimService.submitClaim(dto, userId);
        return Result.success("认领申请提交成功", claim);
    }

    /**
     * 查询物品的认领列表
     * @param itemId 物品ID
     * @return 认领列表
     */
    @GetMapping("/item/{itemId}")
    public Result<List<Claim>> getClaimsByItem(@PathVariable Long itemId) {
        List<Claim> claims = claimService.getClaimsByItemId(itemId);
        return Result.success(claims);
    }

    /**
     * 查询当前用户的认领记录
     * @param request HTTP请求
     * @return 认领列表
     */
    @GetMapping("/my")
    public Result<List<Claim>> getMyClaims(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<Claim> claims = claimService.getClaimsByUserId(userId);
        return Result.success(claims);
    }

    /**
     * 审核认领申请
     * @param id 认领ID
     * @param status 审核状态（1通过/2拒绝）
     * @param reviewRemark 审核备注
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/review/{id}")
    public Result<Void> reviewClaim(@PathVariable Long id,
                                     @RequestParam Integer status,
                                     @RequestParam(required = false) String reviewRemark,
                                     HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        Integer currentUserRole = (Integer) request.getAttribute("currentUserRole");
        claimService.reviewClaim(id, status, reviewRemark, currentUserRole, currentUserId);
        return Result.success();
    }

    /**
     * 删除认领记录
     * @param id 认领ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteClaim(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Integer role = (Integer) request.getAttribute("currentUserRole");
        claimService.deleteClaim(id, userId, role);
        return Result.success();
    }
}
