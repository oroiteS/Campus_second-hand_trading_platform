package com.campus.announcement.controller;
import com.campus.announcement.pojo.Announcement;
import com.campus.announcement.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-8092/announcements")
//@CrossOrigin(origins = "http://localhost:8079", allowCredentials = "true")
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

    @PostMapping
    public ResponseEntity<String> createAnnouncement(@RequestBody Announcement announcement) {
        boolean success = announcementService.addAnnouncement(announcement);
        if (success) {
            return ResponseEntity.ok("公告创建成功");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("公告创建失败");
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateAnnouncement(@RequestBody Announcement announcement) {
        boolean success = announcementService.updateAnnouncement(announcement);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "公告更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 500);
            response.put("message", "公告更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

