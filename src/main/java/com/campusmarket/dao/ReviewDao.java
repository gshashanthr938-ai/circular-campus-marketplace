package com.campusmarket.dao;

import com.campusmarket.db.Db;
import com.campusmarket.model.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDao {
    public List<Review> forListing(long listingId) {
        String sql="SELECT r.rating,r.comment,r.created_at,s.name FROM reviews r JOIN students s ON s.student_id=r.reviewer_id WHERE r.listing_id=? ORDER BY r.created_at DESC";
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,listingId);try(ResultSet rs=ps.executeQuery()) {
                List<Review> out=new ArrayList<>();while(rs.next()){Review r=new Review();r.setRating(rs.getInt(1));r.setComment(rs.getString(2));r.setCreatedAt(rs.getTimestamp(3));r.setReviewerName(rs.getString(4));out.add(r);}return out;
            }
        }catch(SQLException e){throw new RuntimeException(e);}
    }
    public boolean create(long txnId,long buyerId,int rating,String comment) {
        if(rating<1||rating>5||comment==null||comment.isBlank()||comment.length()>800)return false;
        String sql="INSERT INTO reviews(txn_id,listing_id,reviewer_id,rating,comment) "
                + "SELECT t.txn_id,t.listing_id,t.buyer_id,?,? FROM transactions t WHERE t.txn_id=? AND t.buyer_id=? "
                + "AND t.fulfillment_status='PICKUP_COMPLETED' "
                + "AND NOT EXISTS(SELECT 1 FROM reviews r WHERE r.txn_id=t.txn_id)";
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,rating);ps.setString(2,comment.trim());ps.setLong(3,txnId);ps.setLong(4,buyerId);return ps.executeUpdate()==1;
        }catch(SQLException e){throw new RuntimeException(e);}
    }
}
