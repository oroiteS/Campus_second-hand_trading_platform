package com.campus.announcement.dao;
import com.campus.announcement.pojo.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface AnnouncementMapper {

    @Select("SELECT * FROM announcements ORDER BY created_at DESC")
    List<Announcement> selectAll();

    @Select("SELECT * FROM announcements WHERE root_id = #{rootId} ORDER BY created_at DESC")
    List<Announcement> selectAllByRootId(@Param("rootId") String rootId);

    @Select("SELECT * FROM announcements ORDER BY created_at DESC LIMIT #{n}")
    List<Announcement> selectRecent(@Param("n") int n);

    @Select("SELECT * FROM announcements WHERE root_id = #{rootId} ORDER BY created_at DESC LIMIT #{n}")
    List<Announcement> selectRecentByRootId(@Param("rootId") String rootId, @Param("n") int n);

    @Insert("INSERT INTO announcements (Announcement_Id, Root_id, content, visible_status) " +
            "VALUES (#{announcementId}, #{rootId}, #{content}, #{visibleStatus})")
    int insertAnnouncement(Announcement announcement);

    @Update("""
        UPDATE announcements
        SET 
            content = #{content},
            visible_status = #{visibleStatus}
        WHERE Announcement_Id = #{announcementId}
    """)
    int updateAnnouncement(Announcement announcement);
}


