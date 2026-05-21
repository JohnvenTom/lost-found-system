package com.campus.lostfound.service;

import com.campus.lostfound.dao.AnnouncementMapper;
import com.campus.lostfound.entity.Announcement;
import com.campus.lostfound.entity.dto.AnnouncementDTO;
import com.campus.lostfound.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务层，处理系统公告的发布和管理
 */
@Slf4j
@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    /**
     * 获取公告列表
     * @param status 状态筛选（null查询全部）
     * @return 公告列表
     */
    public List<Announcement> getAnnouncementList(Integer status) {
        return announcementMapper.selectList(status);
    }

    /**
     * 根据ID查询公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    public Announcement getAnnouncementById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        return announcement;
    }

    /**
     * 发布公告（管理员操作）
     * @param dto 公告信息
     * @param userId 发布管理员ID
     * @return 公告记录
     */
    public Announcement addAnnouncement(AnnouncementDTO dto, Long userId) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setUserId(userId);
        announcement.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : 0);
        announcement.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        announcementMapper.insert(announcement);
        log.info("公告发布成功: id={}, title={}", announcement.getId(), announcement.getTitle());
        return announcementMapper.selectById(announcement.getId());
    }

    /**
     * 更新公告
     * @param announcement 公告实体
     * @return 更新后的公告
     */
    public Announcement updateAnnouncement(Announcement announcement) {
        announcementMapper.updateById(announcement);
        log.info("公告更新成功: id={}", announcement.getId());
        return announcementMapper.selectById(announcement.getId());
    }

    /**
     * 删除公告
     * @param id 公告ID
     */
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
        log.info("公告删除成功: id={}", id);
    }
}
