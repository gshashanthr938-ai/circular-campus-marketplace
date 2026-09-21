package com.campusmarket.web;

import com.campusmarket.dao.StudentDao;
import com.campusmarket.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final StudentDao studentDao = new StudentDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }
        // Always show fresh profile and sustainability points.
        Student fresh = studentDao.findById(me.getId());
        req.getSession().setAttribute("student", fresh);
        req.setAttribute("student", fresh);
        req.setAttribute("purchaseCount",new com.campusmarket.dao.TransactionDao().purchases(me.getId()).size());
        req.setAttribute("salesCount",new com.campusmarket.dao.TransactionDao().sales(me.getId()).size());
        req.setAttribute("listingCount",new com.campusmarket.dao.ListingDao().findBySeller(me.getId()).size());
        req.setAttribute("notifications",new com.campusmarket.dao.NotificationDao().findFor(me.getId()));
        Web.render(req, resp, "profile.jsp");
    }
}
