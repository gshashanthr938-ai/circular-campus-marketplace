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

@WebServlet("/listing")
public class ListingDetailServlet extends HttpServlet {

    private final ListingDao listingDao = new ListingDao();
    private final CartDao cartDao = new CartDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long id = parseId(req.getParameter("id"));
        Listing listing = id > 0 ? listingDao.findById(id) : null;

        if (listing == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            req.setAttribute("message", "That listing was not found.");
            Web.render(req, resp, "message.jsp");
            return;
        }

        // Record this view in the recently-viewed cookie.
        CookieUtil.recordRecentlyViewed(req, resp, id);

        // Is it already in the current user's cart?
        boolean inCart;
        Student me = Web.currentStudent(req);
        if (me != null) {
            inCart = cartDao.contains(Web.cartSid(req), id);
            req.setAttribute("ownListing", me.getId() == listing.getSellerId());
        } else {
            inCart = CookieUtil.parseIds(CookieUtil.get(req, CookieUtil.GUEST_CART)).contains(id);
            req.setAttribute("ownListing", false);
        }

        req.setAttribute("listing", listing);
        req.setAttribute("inCart", inCart);
        Web.render(req, resp, "listing-detail.jsp");
    }

    private long parseId(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
