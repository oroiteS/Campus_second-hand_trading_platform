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
}


