package com.campusmarket.web;

import com.campusmarket.dao.TransactionDao;
import com.campusmarket.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Transaction History module: buyer (purchases) and seller (sales) views. */
@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    private final TransactionDao transactionDao = new TransactionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }
        Web.consumeFlash(req);
        req.setAttribute("purchases", transactionDao.purchases(me.getId()));
        req.setAttribute("sales", transactionDao.sales(me.getId()));
        Web.render(req, resp, "history.jsp");
    }
}
