package com.campusmarket.db;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/** Curated starter inventory, installed once without resetting existing purchases. */
public final class Catalog {
    private static final List<String[]> ROWS = readRows();
    private static List<String[]> readRows() {
        try (var in = Catalog.class.getResourceAsStream("/catalog.tsv")) {
            if (in == null) throw new IllegalStateException("Missing catalog.tsv");
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines().filter(s -> !s.isBlank()).map(s -> s.split("\t", -1)).toList();
        } catch (IOException e) { throw new IllegalStateException(e); }
    }
    public static String imageFor(String title) {
        for (String[] row : ROWS) if (row[0].equals(title)) return row[4];
        return null;
    }
    public static Map<String,String> categoryImages() {
        Map<String,String> result = new LinkedHashMap<>();
        result.put("Books", "books"); result.put("Stationery", "pencils");
        result.put("Audio", "headphones"); result.put("Electronics", "laptop");
        result.put("Furniture", "desk"); result.put("Hostel Essentials", "lamp");
        result.put("Bags", "backpack"); result.put("Sports", "basketball"); result.put("Music", "guitar");
        return result;
    }
    public static List<String> imageOptions() {
        LinkedHashSet<String> out=new LinkedHashSet<>();
        for(String[] row:ROWS) out.add(row[4]);
        out.addAll(List.of(
                "generated/data-structures-textbook.png",
                "generated/student-calculator.png",
                "generated/foldable-study-table.png",
                "generated/adjustable-led-lamp.png",
                "generated/engineering-drawing-kit.png",
                "generated/bluetooth-headphones.png"));
        out.addAll(List.of("books.svg","electronics.svg","furniture.svg","hostel.svg","clothing.svg","sports.svg","default.svg"));
        return List.copyOf(out);
    }
    public static void applyMissingImages() throws SQLException {
        try(Connection c=Db.getConnection();PreparedStatement p=c.prepareStatement(
                "UPDATE listings SET image_path=? WHERE title=? AND image_path IS NULL")) {
            for(String[] row:ROWS){p.setString(1,row[4]);p.setString(2,row[0]);p.addBatch();}
            p.executeBatch();
        }
    }
    public static void install() throws Exception {
        try (Connection c = Db.getConnection()) {
            try (Statement st = c.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS catalog_updates (version VARCHAR(80) PRIMARY KEY)");
            }
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p = c.prepareStatement("SELECT version FROM catalog_updates WHERE version=?")) {
                    p.setString(1, "expanded-catalog-v1");
                    try (ResultSet rs = p.executeQuery()) { if (rs.next()) return; }
                }
                List<Long> sellers = new ArrayList<>();
                try (PreparedStatement p = c.prepareStatement("SELECT student_id FROM students WHERE email IN (?,?,?) ORDER BY student_id")) {
                    p.setString(1,"asha@campus.edu"); p.setString(2,"rahul@campus.edu"); p.setString(3,"neha@campus.edu");
                    try (ResultSet rs=p.executeQuery()) { while(rs.next()) sellers.add(rs.getLong(1)); }
                }
                if (sellers.isEmpty()) throw new IllegalStateException("Catalog owners are missing");
                try (PreparedStatement p = c.prepareStatement("INSERT INTO listings(seller_id,title,description,category,price,item_condition,status,image_path) VALUES(?,?,?,?,?,?,'AVAILABLE',?)")) {
                    int i=0;
                    for(String[] row:ROWS) {
                        p.setLong(1,sellers.get(i++ % sellers.size())); p.setString(2,row[0]);
                        p.setString(3,row[5]); p.setString(4,row[1]); p.setBigDecimal(5,new BigDecimal(row[2]));
                        p.setString(6,row[3]); p.setString(7,row[4]); p.addBatch();
                    }
                    p.executeBatch();
                }
                try (PreparedStatement p=c.prepareStatement("INSERT INTO catalog_updates(version) VALUES(?)")) {
                    p.setString(1,"expanded-catalog-v1"); p.executeUpdate();
                }
                c.commit();
            } catch(Exception e) { c.rollback(); throw e; }
        }
    }
}
