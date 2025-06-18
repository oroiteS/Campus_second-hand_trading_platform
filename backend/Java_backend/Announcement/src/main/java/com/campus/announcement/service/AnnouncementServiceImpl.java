package com.campus.announcement.service;
import com.campus.announcement.dao.AnnouncementMapper;
import com.campus.announcement.pojo.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

}

