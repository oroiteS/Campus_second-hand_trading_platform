package com.campus.announcement.controller;
import com.campus.announcement.pojo.Announcement;
import com.campus.announcement.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;


    @GetMapping
    public ResponseEntity<List<Announcement>> getAnnouncements(
            @RequestParam(value = "n", required = false) Integer n,
            @RequestParam(value = "rootId", required = false) String rootId) {
        List<Announcement> result = announcementService.getAnnouncements(n, rootId);
        return ResponseEntity.ok(result);
    }

}

