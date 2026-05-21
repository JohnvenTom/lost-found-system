package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Announcement;
import com.campus.lostfound.entity.dto.AnnouncementDTO;
import com.campus.lostfound.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 公告控制器，提供系统公告的发布和查询接口
 */
@Slf4j
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 获取公告列表
     * @param status 状态筛选（可选）
     * @return 公告列表
     */
    @GetMapping("/list")
    public Result<List<Announcement>> getAnnouncementList(@RequestParam(required = false) Integer status) {
        List<Announcement> announcements = announcementService.getAnnouncementList(status);
        return Result.success(announcements);
    }

    /**
     * 查询公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/detail/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        return Result.success(announcement);
    }

    /**
     * 管理员：发布公告
     * @param dto 公告信息
     * @param request HTTP请求
     * @return 公告记录
     */
    @PostMapping("/add")
    public Result<Announcement> addAnnouncement(@Validated @RequestBody AnnouncementDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Announcement announcement = announcementService.addAnnouncement(dto, userId);
        return Result.success(announcement);
    }

    /**
     * 管理员：更新公告
     * @param id 公告ID
     * @param announcement 公告实体
     * @return 更新后的公告
     */
    @PutMapping("/update/{id}")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        Announcement updated = announcementService.updateAnnouncement(announcement);
        return Result.success(updated);
    }

    /**
     * 管理员：删除公告
     * @param id 公告ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }
}
