package com.campusmarket.dao;

import com.campusmarket.db.Db;
import com.campusmarket.model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDao {
    public List<Notification> findFor(long studentId) {
        String sql="SELECT * FROM notifications WHERE student_id=? ORDER BY created_at DESC";
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,studentId);
            try(ResultSet rs=ps.executeQuery()) {
                List<Notification> out=new ArrayList<>();
                while(rs.next()) {
                    Notification n=new Notification(); n.setId(rs.getLong("notification_id"));
                    n.setMessage(rs.getString("message")); n.setLinkPath(rs.getString("link_path"));
                    n.setRead(rs.getBoolean("is_read")); n.setCreatedAt(rs.getTimestamp("created_at")); out.add(n);
                }
                return out;
            }
        } catch(SQLException e) { throw new RuntimeException(e); }
    }
    public int unreadCount(long studentId) {
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(
                "SELECT COUNT(*) FROM notifications WHERE student_id=? AND is_read=FALSE")) {
            ps.setLong(1,studentId); try(ResultSet rs=ps.executeQuery()){rs.next();return rs.getInt(1);}
        } catch(SQLException e) { throw new RuntimeException(e); }
    }
    public void markAllRead(long studentId) {
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(
                "UPDATE notifications SET is_read=TRUE WHERE student_id=?")) {
            ps.setLong(1,studentId); ps.executeUpdate();
        } catch(SQLException e) { throw new RuntimeException(e); }
    }
}
