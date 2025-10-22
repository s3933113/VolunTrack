

import org.junit.jupiter.api.Test;

import voluntrack.service.CartService;
import voluntrack.service.RegistrationService;
import voluntrack.util.IdUtil;
import voluntrack.util.TimeUtil;

import static org.junit.jupiter.api.Assertions.*;

class UtilsAndServiceUnitTests {

    // 1) IdUtil.zeroPad4 — จัดรูปเป็น 4 หลัก
    @Test
    void zeroPad4_basic() {
        assertEquals("0001", IdUtil.zeroPad4(1));
        assertEquals("0123", IdUtil.zeroPad4(123));
        assertEquals("9999", IdUtil.zeroPad4(9999));
        // ถ้าเกิน 4 หลักควรปล่อยตามจริง (ไม่ตัดทิ้ง)
        assertEquals("12345", IdUtil.zeroPad4(12345));
    }

    // 2) TimeUtil.nowIso — time pattern is ISO-8601 (roughly)
    @Test
    void nowIso_formatLooksIso() {
        String now = TimeUtil.nowIso();
        assertNotNull(now);
        // Example pattern : 2025-10-22T14:03:59
        assertTrue(now.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*"),
                "nowIso should look like ISO-8601, but was: " + now);
    }

    // 3) RegistrationService.isValidConfirmationCode — must be 6 digits
    @Test
    void confirmCode_validator() {
        RegistrationService rs = new RegistrationService();
        assertTrue(rs.isValidConfirmationCode("123456"));
        assertFalse(rs.isValidConfirmationCode(null));
        assertFalse(rs.isValidConfirmationCode(""));         // empty
        assertFalse(rs.isValidConfirmationCode("12345"));    // 5 digits
        assertFalse(rs.isValidConfirmationCode("1234567"));  // 7 digits
        assertFalse(rs.isValidConfirmationCode("12A456"));   // has letter
    }

    // 4) CartService — addOrUpdate correct calculation
    @Test
    void cart_addAndTotalContribution() {
        CartService cart = new CartService();
        // fill project(id=10) wage=25, choose 2 slot, 3 hr/slot
        // contribution = 25 * 3 * 2 = 150
        String res = cart.addOrUpdate(10, "Any Project", 25, 2, 3);
        assertEquals("SUCCESS", res);
        assertEquals(150, cart.totalContribution());
    }

    // 5) CartService — clear Must empty and total is 0
    @Test
    void cart_clearEmptiesCart() {
        CartService cart = new CartService();
        cart.addOrUpdate(7, "P1", 10, 1, 2);  // 10 * 2 * 1 = 20
        assertTrue(cart.totalContribution() > 0);
        cart.clear();
        assertEquals(0, cart.totalContribution());
        assertTrue(cart.isEmpty());
    }
}
