package com.campusmarket;
import com.campusmarket.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PasswordTest {
 @Test void saltedHashesAndVerification() {
  String a=PasswordUtil.hash("student-password"), b=PasswordUtil.hash("student-password");
  assertNotEquals(a,b); assertTrue(a.length()<=128);
  assertTrue(PasswordUtil.matches("student-password",a)); assertFalse(PasswordUtil.matches("wrong",a));
 }
 @Test void legacyAndMalformedHashes() {
  assertTrue(PasswordUtil.matches("password","5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"));
  assertFalse(PasswordUtil.matches("password","pbkdf2$broken")); assertFalse(PasswordUtil.matches(null,null));
 }
}
