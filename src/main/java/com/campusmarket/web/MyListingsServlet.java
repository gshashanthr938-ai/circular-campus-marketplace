package com.campusmarket.web;

import com.campusmarket.dao.ListingDao;
import com.campusmarket.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/my-listings")
public class MyListingsServlet extends HttpServlet {

    private final ListingDao listingDao = new ListingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }
        Web.consumeFlash(req);
        req.setAttribute("listings", listingDao.findBySeller(me.getId()));
        Web.render(req, resp, "my-listings.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }
        if ("delete".equals(req.getParameter("action"))) {
            long id = parseId(req.getParameter("id"));
            if (id > 0) {
                listingDao.delete(id, me.getId());
                Web.setFlash(req, "Listing removed.");
            }
        }
        Web.redirect(req, resp, "/my-listings");
    }

    private long parseId(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
