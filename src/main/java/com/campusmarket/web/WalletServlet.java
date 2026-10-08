package com.campusmarket.web;

import com.campusmarket.dao.StudentDao;
import com.campusmarket.dao.WalletDao;
import com.campusmarket.model.Student;
import com.campusmarket.service.WalletService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Displays refund money and lets its owner withdraw through UPI or net banking. */
@WebServlet("/wallet")
public class WalletServlet extends HttpServlet {
    private final StudentDao studentDao = new StudentDao();
    private final WalletDao walletDao = new WalletDao();
    private final WalletService walletService = new WalletService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        if (me == null) { Web.redirect(req, resp, "/login"); return; }
        Web.consumeFlash(req);
        Student fresh = studentDao.findById(me.getId());
        req.getSession().setAttribute("student", fresh);
        req.setAttribute("student", fresh);
        req.setAttribute("walletEntries", walletDao.findFor(me.getId()));
        Web.render(req, resp, "wallet.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student me = Web.currentStudent(req);
        if (me == null) { Web.redirect(req, resp, "/login"); return; }
        WalletService.Result result = walletService.withdraw(me.getId(), req.getParameter("amount"),
                req.getParameter("method"), req.getParameter("destinationDetail"));
        if (result.success) Web.setFlash(req, result.message); else Web.setFlashError(req, result.message);
        Web.redirect(req, resp, "/wallet");
    }
}
