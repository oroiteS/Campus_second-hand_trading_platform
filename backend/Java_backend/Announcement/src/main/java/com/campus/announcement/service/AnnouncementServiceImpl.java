package com.campus.announcement.service;
import com.campus.announcement.dao.AnnouncementMapper;
import com.campus.announcement.pojo.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> getAnnouncements(Integer n, String rootId) {
        if (rootId != null && n != null) {
            return announcementMapper.selectRecentByRootId(rootId, n);
        } else if (rootId != null) {
            return announcementMapper.selectAllByRootId(rootId);
        } else if (n != null) {
            return announcementMapper.selectRecent(n);
        } else {
            return announcementMapper.selectAll();
        }
    }

    @Override
    public boolean addAnnouncement(Announcement announcement) {
        if (announcement.getAnnouncementId() == null || announcement.getAnnouncementId().isEmpty()) {
            // 你也可以使用 UUID 或自定义生成策略
            announcement.setAnnouncementId(UUID.randomUUID().toString().substring(0, 10));
        }
        // 默认值：可见
        if (announcement.getVisibleStatus() == false) {
            announcement.setVisibleStatus(false);
        }
        return announcementMapper.insertAnnouncement(announcement) > 0;
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        return announcementMapper.updateAnnouncement(announcement) > 0;
    }
}

