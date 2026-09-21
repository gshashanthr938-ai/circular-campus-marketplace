package com.campusmarket;

import com.campusmarket.util.CookieUtil;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CookieUtilTest {
    @Test void usesCookieSafeSeparatorAndReadsLegacyValues() {
        assertEquals("8.4.12",CookieUtil.joinIds(List.of(8L,4L,12L)));
        assertEquals(List.of(8L,4L,12L),CookieUtil.parseIds("8.4.12"));
        assertEquals(List.of(8L,4L,12L),CookieUtil.parseIds("8,4,12"));
    }
}
