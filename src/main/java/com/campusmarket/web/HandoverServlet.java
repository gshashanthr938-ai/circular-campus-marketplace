package com.campusmarket.web;

import com.campusmarket.dao.TransactionDao;
import com.campusmarket.model.Student;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Confirms that a seller physically handed the item to its buyer. */
@WebServlet("/handover")
public class HandoverServlet extends HttpServlet {
    private final TransactionDao transactionDao = new TransactionDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }
        boolean confirmed = false;
        try {
            confirmed = transactionDao.confirmHandover(
                    Long.parseLong(req.getParameter("txnId")), me.getId(), req.getParameter("handoverCode"));
        } catch (RuntimeException ignored) {
            // A malformed or stale request receives the same safe message as an incorrect code.
        }
        Web.setFlash(req, confirmed
                ? "Pickup confirmed. The buyer can now leave a verified review."
                : "Pickup was not confirmed. Check the buyer's six-digit code and try again.");
        Web.redirect(req, resp, "/history");
    }
}
