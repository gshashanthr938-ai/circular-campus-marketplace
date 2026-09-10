package com.campusmarket.web;

import com.campusmarket.dao.ListingDao;
import com.campusmarket.model.Listing;
import com.campusmarket.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/** Listings module: create a new listing or edit an existing one (owner only). */
@WebServlet("/sell")
public class SellServlet extends HttpServlet {

    private final ListingDao listingDao = new ListingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }
        long id = parseId(req.getParameter("id"));
        if (id > 0) {
            Listing listing = listingDao.findById(id);
            if (listing == null || listing.getSellerId() != me.getId()) {
                req.setAttribute("message", "You can only edit your own listings.");
                Web.render(req, resp, "message.jsp");
                return;
            }
            req.setAttribute("listing", listing);
        }
        Web.render(req, resp, "sell.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }

        long id = parseId(req.getParameter("id"));
        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        String category = trim(req.getParameter("category"));
        String condition = trim(req.getParameter("condition"));
        BigDecimal price = parsePrice(req.getParameter("price"));

        if (title == null || title.isEmpty() || category == null || category.isEmpty()
                || condition == null || condition.isEmpty() || price == null
                || price.signum() < 0) {
            req.setAttribute("error", "Please fill every field with a valid, non-negative price.");
            req.setAttribute("listing", rebuild(id, title, description, category, price, condition, me));
            Web.render(req, resp, "sell.jsp");
            return;
        }

        if (id > 0) {
            listingDao.update(id, me.getId(), title, description, category, price, condition);
            Web.setFlash(req, "Listing updated.");
        } else {
            listingDao.create(me.getId(), title, description, category, price, condition);
            Web.setFlash(req, "Listing published!");
        }
        Web.redirect(req, resp, "/my-listings");
    }

    private Listing rebuild(long id, String title, String desc, String cat,
                            BigDecimal price, String cond, Student me) {
        Listing l = new Listing();
        l.setId(id);
        l.setSellerId(me.getId());
        l.setTitle(title);
        l.setDescription(desc);
        l.setCategory(cat);
        l.setPrice(price);
        l.setCondition(cond);
        return l;
    }

    private long parseId(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
    }

    private BigDecimal parsePrice(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
