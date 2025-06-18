package com.campus.announcement.pojo;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class Announcement {
    private String announcementId;
    private String rootId;
    private LocalDateTime createdAt;
    private String content;
    private Boolean visibleStatus;
}

