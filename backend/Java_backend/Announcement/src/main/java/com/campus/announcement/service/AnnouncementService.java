package com.campus.announcement.service;
import com.campus.announcement.pojo.Announcement;

import java.util.List;
public interface AnnouncementService {
    List<Announcement> getAnnouncements(Integer n, String rootId);

}

