package com.campusmarket.dao;

import com.campusmarket.db.Db;
import java.sql.*;

public class WaitlistDao {
    public void join(long listingId, long studentId) {
        try(Connection c=Db.getConnection()) { join(c,listingId,studentId); }
        catch(SQLException e) { throw new RuntimeException(e); }
    }
    public boolean contains(long listingId,long studentId) {
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(
                "SELECT 1 FROM waitlist WHERE listing_id=? AND student_id=?")) {
            ps.setLong(1,listingId);ps.setLong(2,studentId);try(ResultSet rs=ps.executeQuery()){return rs.next();}
        } catch(SQLException e){throw new RuntimeException(e);}
    }
    public void joinUnavailableItems(long studentId,String sessionId) {
        String sql="SELECT DISTINCT l.listing_id,l.title FROM cart_items ci JOIN listings l ON l.listing_id=ci.listing_id "
                + "WHERE ci.session_id=? AND l.status<>'AVAILABLE' AND l.seller_id<>?";
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,sessionId);ps.setLong(2,studentId);
            try(ResultSet rs=ps.executeQuery()) {
                while(rs.next()) {
                    long id=rs.getLong(1);
                    if(join(c,id,studentId)) {
                        notify(c,studentId,"Another student purchased “"+rs.getString(2)+"” first. You are on its waitlist.","/listing?id="+id);
                    }
                }
            }
        } catch(SQLException e){throw new RuntimeException(e);}
    }
    public void notifyAvailable(long listingId,String title) {
        try(Connection c=Db.getConnection()) {
            try(PreparedStatement ps=c.prepareStatement(
                    "INSERT INTO notifications(student_id,message,link_path) SELECT student_id,?,? FROM waitlist WHERE listing_id=?")) {
                ps.setString(1,"“"+title+"” is available again. First completed checkout wins.");
                ps.setString(2,"/listing?id="+listingId);ps.setLong(3,listingId);ps.executeUpdate();
            }
            try(PreparedStatement ps=c.prepareStatement("DELETE FROM waitlist WHERE listing_id=?")){
                ps.setLong(1,listingId);ps.executeUpdate();
            }
        } catch(SQLException e){throw new RuntimeException(e);}
    }
    private boolean join(Connection c,long listingId,long studentId)throws SQLException {
        try(PreparedStatement ps=c.prepareStatement(
                "INSERT INTO waitlist(listing_id,student_id) SELECT ?,? WHERE NOT EXISTS "
                        + "(SELECT 1 FROM waitlist WHERE listing_id=? AND student_id=?)")) {
            ps.setLong(1,listingId);ps.setLong(2,studentId);ps.setLong(3,listingId);ps.setLong(4,studentId);
            return ps.executeUpdate()==1;
        }
    }
    private void notify(Connection c,long studentId,String message,String link)throws SQLException {
        try(PreparedStatement ps=c.prepareStatement(
                "INSERT INTO notifications(student_id,message,link_path) VALUES(?,?,?)")) {
            ps.setLong(1,studentId);ps.setString(2,message);ps.setString(3,link);ps.executeUpdate();
        }
    }
}
