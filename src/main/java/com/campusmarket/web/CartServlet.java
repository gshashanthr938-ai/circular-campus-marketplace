package com.campusmarket.web;

import com.campusmarket.dao.CartDao;
import com.campusmarket.dao.ListingDao;
import com.campusmarket.model.Listing;
import com.campusmarket.model.Student;
import com.campusmarket.util.CookieUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart module.
 *  - Logged-in users: cart persists in the DB (cart_items) keyed by session id.
 *  - Guests: cart lives in the "guest_cart" cookie.
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final CartDao cartDao = new CartDao();
    private final ListingDao listingDao = new ListingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        List<Listing> items = new ArrayList<>();

        if (me != null) {
            items = cartDao.items(Web.cartSid(req));
        } else {
            for (Long id : CookieUtil.parseIds(CookieUtil.get(req, CookieUtil.GUEST_CART))) {
                Listing l = listingDao.findById(id);
                if (l != null && l.isAvailable()) {
                    items.add(l);
                }
            }
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Listing l : items) {
            total = total.add(l.getPrice());
        }

        Web.consumeFlash(req);
        req.setAttribute("items", items);
        req.setAttribute("total", total);
        Web.render(req, resp, "cart.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        long id = parseId(req.getParameter("id"));
        Student me = Web.currentStudent(req);

        if (id > 0 && "add".equals(action)) {
            add(req, resp, me, id);
        } else if (id > 0 && "remove".equals(action)) {
            remove(req, resp, me, id);
        }
        // Return to wherever the user was, or the cart page.
        String back = req.getParameter("back");
        Web.redirect(req, resp, back != null && !back.isEmpty() ? back : "/cart");
    }

    private void add(HttpServletRequest req, HttpServletResponse resp, Student me, long id) {
        Listing l = listingDao.findById(id);
        if (l == null || !l.isAvailable()) {
            Web.setFlash(req, "That item is no longer available.");
            return;
        }
        if (me != null) {
            if (l.getSellerId() == me.getId()) {
                Web.setFlash(req, "You can't buy your own listing.");
                return;
            }
            cartDao.add(Web.cartSid(req), id);
        } else {
            CookieUtil.addToGuestCart(req, resp, id);
        }
        Web.setFlash(req, "Added to cart.");
    }

    private void remove(HttpServletRequest req, HttpServletResponse resp, Student me, long id) {
        if (me != null) {
            cartDao.remove(Web.cartSid(req), id);
        } else {
            CookieUtil.removeFromGuestCart(req, resp, id);
        }
        Web.setFlash(req, "Removed from cart.");
    }

    private long parseId(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
