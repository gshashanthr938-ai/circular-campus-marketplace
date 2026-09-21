package com.campusmarket.web;

import com.campusmarket.dao.CartDao;
import com.campusmarket.dao.ListingDao;
import com.campusmarket.model.Listing;
import com.campusmarket.model.Student;
import com.campusmarket.util.CookieUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/** Small shared helpers used across the servlets. */
public final class Web {

    private Web() {
    }

    public static Student currentStudent(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (Student) s.getAttribute("student");
    }

    public static boolean isLoggedIn(HttpServletRequest req) {
        return currentStudent(req) != null;
    }

    /** The HttpSession id used as the cart key in the cart_items table. */
    public static String cartSid(HttpServletRequest req) {
        return req.getSession().getId();
    }

    /** Forward to a JSP view under /WEB-INF/views/. */
    public static void render(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }

    /** Redirect using the app context path as prefix. */
    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        if (path == null || !path.startsWith("/") || path.startsWith("//") || path.contains("\\") || path.contains("\r") || path.contains("\n")) path="/browse";
        resp.sendRedirect(req.getContextPath() + path);
    }

    /** Store a one-time message shown on the next page. */
    public static void setFlash(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flash", message);
    }

    /** Read + clear the one-time flash message into a request attribute. */
    public static void consumeFlash(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("flash") != null) {
            req.setAttribute("flash", s.getAttribute("flash"));
            s.removeAttribute("flash");
        }
    }

    /**
     * Core cookie->session demonstration: when a guest logs in, move every
     * item from their cookie-based guest cart into the session/DB cart, then
     * delete the cookie.
     */
    public static void migrateGuestCartToSession(HttpServletRequest req, HttpServletResponse resp) {
        String cookie = CookieUtil.get(req, CookieUtil.GUEST_CART);
        List<Long> ids = CookieUtil.parseIds(cookie);
        if (ids.isEmpty()) {
            return;
        }
        CartDao cartDao = new CartDao();
        ListingDao listingDao = new ListingDao();
        Student me = currentStudent(req);
        String sid = cartSid(req);
        for (Long id : ids) {
            Listing l = listingDao.findById(id);
            if (l != null && l.isAvailable() && l.getSellerId() != me.getId()) {
                cartDao.add(sid, id);
            }
        }
        CookieUtil.delete(resp, CookieUtil.GUEST_CART);
    }
}
