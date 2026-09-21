package com.campusmarket;

import com.campusmarket.service.ListingPolicyService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ListingPolicyTest {
    private final ListingPolicyService policy = new ListingPolicyService();

    @Test void acceptsReasonableCampusListing() {
        var result=policy.evaluate("Data Structures textbook","Used for one semester","Books",new BigDecimal("550"),"Good");
        assertTrue(result.allowed());
    }

    @Test void blocksUnreasonablePrice() {
        var result=policy.evaluate("Used textbook","Clean pages","Books",new BigDecimal("2900"),"Fair");
        assertFalse(result.allowed());
        assertTrue(result.message().contains("too high"));
    }

    @Test void blocksProhibitedCampusItem() {
        var result=policy.evaluate("Vape kit","Unused","Electronics",new BigDecimal("500"),"Like New");
        assertFalse(result.allowed());
        assertTrue(result.message().contains("prohibited"));
    }
}
