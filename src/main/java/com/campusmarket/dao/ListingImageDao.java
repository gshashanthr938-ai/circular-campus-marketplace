package com.campusmarket.dao;

import com.campusmarket.db.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListingImageDao {
    public List<String> forListing(long listingId) {
        String sql="SELECT image_path FROM listing_images WHERE listing_id=? ORDER BY position_no";
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,listingId);try(ResultSet rs=ps.executeQuery()){
                List<String> out=new ArrayList<>();while(rs.next())out.add(rs.getString(1));return out;
            }
        }catch(SQLException e){throw new RuntimeException(e);}
    }

    public List<String> allPaths() {
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(
                "SELECT DISTINCT image_path FROM listing_images ORDER BY image_path");ResultSet rs=ps.executeQuery()) {
            List<String> out=new ArrayList<>();while(rs.next())out.add(rs.getString(1));return out;
        }catch(SQLException e){throw new RuntimeException(e);}
    }

    public void replace(long listingId,List<String> paths) {
        try(Connection c=Db.getConnection()) {
            c.setAutoCommit(false);
            try(PreparedStatement d=c.prepareStatement("DELETE FROM listing_images WHERE listing_id=?")){
                d.setLong(1,listingId);d.executeUpdate();
            }
            try(PreparedStatement p=c.prepareStatement(
                    "INSERT INTO listing_images(listing_id,image_path,position_no) VALUES(?,?,?)")){
                for(int i=0;i<paths.size();i++){p.setLong(1,listingId);p.setString(2,paths.get(i));p.setInt(3,i+1);p.addBatch();}
                p.executeBatch();
            }
            try(PreparedStatement p=c.prepareStatement("UPDATE listings SET image_path=? WHERE listing_id=?")){
                p.setString(1,paths.get(0));p.setLong(2,listingId);p.executeUpdate();
            }
            c.commit();
        }catch(SQLException e){throw new RuntimeException(e);}
    }
}
