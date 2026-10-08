package com.campusmarket;
import com.campusmarket.db.Db;
import com.campusmarket.service.CheckoutService;
import com.campusmarket.dao.ReviewDao;
import com.campusmarket.dao.TransactionDao;
import com.campusmarket.service.RefundService;
import com.campusmarket.service.WalletService;
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
    String textValue(String sql)throws Exception {try(Connection c=Db.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){r.next();return r.getString(1);}}
    void cart(String sid,int item)throws Exception {sql("INSERT INTO cart_items(session_id,listing_id) VALUES('"+sid+"',"+item+")");}
    @Test void successfulUpiPaymentAndRepeatIsSafe() throws Exception {
        cart("one",1); assertTrue(new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success);
        assertEquals(0,new BigDecimal("1000").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=1")));
        assertEquals(0,new BigDecimal("100").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=2")));
        assertEquals(1,value("SELECT COUNT(*) FROM transactions").intValue());
        assertEquals(1,value("SELECT COUNT(*) FROM transactions WHERE payment_method='UPI' AND payment_status='COMPLETED' AND payment_reference IS NOT NULL AND terms_accepted_at IS NOT NULL").intValue());
        assertEquals(1,value("SELECT COUNT(*) FROM transactions WHERE fulfillment_status='AWAITING_PICKUP' AND handover_code IS NOT NULL AND LENGTH(handover_code)=6 AND pickup_completed_at IS NULL").intValue());
        assertEquals(10,value("SELECT sustainability_points FROM students WHERE student_id=1").intValue());
        assertFalse(new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success);
    }
    @Test void pickupCodeProvesHandoverAndUnlocksReview() throws Exception {
        cart("one",1);
        assertTrue(new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success);
        String code=textValue("SELECT handover_code FROM transactions WHERE listing_id=1");
        long txnId=value("SELECT txn_id FROM transactions WHERE listing_id=1").longValue();
        assertFalse(new ReviewDao().create(txnId,1,5,"Too early"));
        assertFalse(new TransactionDao().confirmHandover(txnId,3,code));
        assertFalse(new TransactionDao().confirmHandover(txnId,2,"000000".equals(code)?"999999":"000000"));
        assertTrue(new TransactionDao().confirmHandover(txnId,2,code));
        assertFalse(new TransactionDao().confirmHandover(txnId,2,code));
        assertEquals(1,value("SELECT COUNT(*) FROM transactions WHERE txn_id="+txnId+" AND fulfillment_status='PICKUP_COMPLETED' AND pickup_completed_at IS NOT NULL").intValue());
        assertTrue(new ReviewDao().create(txnId,1,5,"Item matched the photos and pickup was smooth."));
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
    @Test void walletRefundReopensItemAndCanBeWithdrawn() throws Exception {
        cart("one",1);
        assertTrue(new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success);
        long txnId=value("SELECT txn_id FROM transactions WHERE listing_id=1").longValue();

        RefundService.Result refund=new RefundService().refund(1,txnId,"WALLET","Seller cannot complete the pickup");
        assertTrue(refund.success);
        assertEquals(0,new BigDecimal("1300.00").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=1")));
        assertEquals("AVAILABLE",textValue("SELECT status FROM listings WHERE listing_id=1"));
        assertEquals(1,value("SELECT COUNT(*) FROM transactions WHERE txn_id="+txnId+" AND payment_status='REFUNDED' AND fulfillment_status='CANCELLED' AND refund_method='WALLET' AND refunded_at IS NOT NULL").intValue());
        assertEquals(1,value("SELECT COUNT(*) FROM wallet_transactions WHERE student_id=1 AND entry_type='REFUND_CREDIT' AND amount=300").intValue());
        assertEquals(0,value("SELECT sustainability_points FROM students WHERE student_id=1").intValue());
        assertFalse(new RefundService().refund(1,txnId,"WALLET","Duplicate refund attempt").success);
        assertFalse(new TransactionDao().confirmHandover(txnId,2,textValue("SELECT handover_code FROM transactions WHERE txn_id="+txnId)));
        assertFalse(new ReviewDao().create(txnId,1,5,"Cancelled order"));

        WalletService.Result withdrawal=new WalletService().withdraw(1,"200","UPI","buyer@bank");
        assertTrue(withdrawal.success);
        assertEquals(0,new BigDecimal("1100.00").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=1")));
        assertEquals(1,value("SELECT COUNT(*) FROM wallet_transactions WHERE student_id=1 AND entry_type='WITHDRAWAL' AND amount=-200").intValue());
        assertFalse(new WalletService().withdraw(1,"5000","UPI","buyer@bank").success);
        assertEquals(0,new BigDecimal("1100.00").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=1")));
    }
    @Test void originalPaymentRefundDoesNotCreditWallet() throws Exception {
        cart("one",1);
        assertTrue(new CheckoutService().checkout(1,"one","NET_BANKING","SBI",true).success);
        long txnId=value("SELECT txn_id FROM transactions WHERE listing_id=1").longValue();
        assertTrue(new RefundService().refund(1,txnId,"ORIGINAL_METHOD","Item is no longer needed").success);
        assertEquals(0,new BigDecimal("1000.00").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=1")));
        assertEquals(0,value("SELECT COUNT(*) FROM wallet_transactions WHERE student_id=1").intValue());
        assertEquals(1,value("SELECT COUNT(*) FROM transactions WHERE txn_id="+txnId+" AND refund_method='ORIGINAL_METHOD' AND refund_reference IS NOT NULL").intValue());
    }
    @Test void completedPickupCannotBeRefunded() throws Exception {
        cart("one",1);
        assertTrue(new CheckoutService().checkout(1,"one","UPI","buyer@bank",true).success);
        long txnId=value("SELECT txn_id FROM transactions WHERE listing_id=1").longValue();
        String code=textValue("SELECT handover_code FROM transactions WHERE txn_id="+txnId);
        assertTrue(new TransactionDao().confirmHandover(txnId,2,code));
        assertFalse(new RefundService().refund(1,txnId,"WALLET","Attempt after completed pickup").success);
        assertEquals("SOLD",textValue("SELECT status FROM listings WHERE listing_id=1"));
        assertEquals(0,new BigDecimal("1000.00").compareTo(value("SELECT wallet_balance FROM students WHERE student_id=1")));
    }
}
