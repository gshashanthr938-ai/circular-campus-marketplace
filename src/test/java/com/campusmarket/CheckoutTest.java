package com.campusmarket;
import com.campusmarket.db.Db;
import com.campusmarket.service.CheckoutService;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutTest {
    @BeforeAll static void configure() {
        System.setProperty("db.driver","org.h2.Driver");
        System.setProperty("db.url","jdbc:h2:mem:checkout_tests;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000");
        System.setProperty("db.user","sa"); System.setProperty("db.password","");
    }
    @BeforeEach void setup() throws Exception {
        try(Connection c=Db.getConnection(); Statement st=c.createStatement()) {
            st.execute("DROP ALL OBJECTS");
            String ddl=new String(getClass().getResourceAsStream("/schema.sql").readAllBytes(),StandardCharsets.UTF_8);
            for(String sql:ddl.split(";")) if(!sql.isBlank()) st.execute(sql);
            st.execute("INSERT INTO students(student_id,name,email,password,wallet_balance) VALUES(1,'Buyer','a@a.com','x',1000),(2,'Seller','b@b.com','x',100),(3,'Other buyer','c@c.com','x',1000)");
            st.execute("INSERT INTO listings(listing_id,seller_id,title,category,price,item_condition) VALUES(1,2,'Book','Books',300,'Good'),(2,2,'Laptop','Electronics',2000,'Good')");
        }
    }
    void sql(String sql)throws Exception {try(Connection c=Db.getConnection();Statement s=c.createStatement()){s.execute(sql);}}
    BigDecimal value(String sql)throws Exception {try(Connection c=Db.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){r.next();return r.getBigDecimal(1);}}
    void cart(String sid,int item)throws Exception {sql("INSERT INTO cart_items(session_id,listing_id) VALUES('"+sid+"',"+item+")");}
    @Test void successfulUpiPaymentAndRepeatIsSafe() throws Exception {
        cart("one",1); assertTrue(new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success);
        assertEquals(0,new BigDecimal("1000").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=1")));
        assertEquals(0,new BigDecimal("100").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=2")));
        assertEquals(1,value("SELECT COUNT(*) FROM transactions").intValue());
        assertEquals(1,value("SELECT COUNT(*) FROM transactions WHERE payment_method='UPI' AND payment_status='COMPLETED' AND payment_reference IS NOT NULL AND terms_accepted_at IS NOT NULL").intValue());
        assertEquals(10,value("SELECT sustainability_points FROM students WHERE student_id=1").intValue());
        assertFalse(new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success);
    }
    @Test void invalidPaymentDoesNotClaimItem() throws Exception {
        cart("one",2); assertFalse(new CheckoutService().checkout(1,"one","UPI","not-a-upi-id",true).success);
        assertEquals(0,value("SELECT COUNT(*) FROM transactions").intValue());
        assertEquals(1,value("SELECT COUNT(*) FROM cart_items").intValue());
    }
    @Test void duplicateCartRowsDoNotChargeTwice() throws Exception {
        cart("one",1);cart("one",1);assertTrue(new CheckoutService().checkout(1,"one","NET_BANKING","SBI",true).success);
        assertEquals(1,value("SELECT COUNT(*) FROM transactions").intValue());
    }
    @Test void ownListingRejected() throws Exception {cart("seller",1);assertFalse(new CheckoutService().checkout(2,"seller","UPI","seller@bank",true).success);}
    @Test void termsAreRequiredBeforePurchase() throws Exception {
        cart("one",1);assertFalse(new CheckoutService().checkout(1,"one","UPI","buyer@bank",false).success);
        assertEquals(0,value("SELECT COUNT(*) FROM transactions").intValue());
        assertEquals(1,value("SELECT COUNT(*) FROM cart_items").intValue());
    }
    @Test void soldListingRejected() throws Exception {
        cart("one",1);sql("UPDATE listings SET status='SOLD' WHERE listing_id=1");
        assertFalse(new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success);
        assertEquals(0,value("SELECT COUNT(*) FROM transactions").intValue());
    }
    @Test void concurrentBuyersCannotBuySameItemTwice() throws Exception {
        cart("one",1);cart("two",1);
        ExecutorService pool=Executors.newFixedThreadPool(2); CountDownLatch go=new CountDownLatch(1);
        try {
            Future<Boolean> a=pool.submit(()->{go.await();return new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success;});
            Future<Boolean> b=pool.submit(()->{go.await();return new CheckoutService().checkout(3,"two","NET_BANKING","HDFC",true).success;});
            go.countDown();
            boolean firstWon=a.get(15,TimeUnit.SECONDS),secondWon=b.get(15,TimeUnit.SECONDS);
            assertNotEquals(firstWon,secondWon);
            assertEquals(1,value("SELECT COUNT(*) FROM transactions").intValue());
            assertEquals(2100,value("SELECT SUM(wallet_balance) FROM students").intValue());
            long losingBuyer=firstWon ? 3 : 1;
            assertEquals(1,value("SELECT COUNT(*) FROM waitlist WHERE student_id="+losingBuyer+" AND listing_id=1").intValue());
            assertEquals(1,value("SELECT COUNT(*) FROM notifications WHERE student_id="+losingBuyer).intValue());
        } finally {pool.shutdownNow();}
    }
}
