package com.campusmarket.web;

import com.campusmarket.model.Student;
import com.campusmarket.service.RefundService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Buyer-initiated cancellation before the verified physical handover. */
@WebServlet("/refund")
public class RefundServlet extends HttpServlet {
    private final RefundService refundService = new RefundService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student me = Web.currentStudent(req);
        if (me == null) { Web.redirect(req, resp, "/login"); return; }
        RefundService.Result result;
        try {
            result = refundService.refund(me.getId(), Long.parseLong(req.getParameter("txnId")),
                    req.getParameter("refundMethod"), req.getParameter("reason"));
        } catch (RuntimeException ex) {
            result = null;
        }
        if (result != null && result.success) Web.setFlash(req, result.message);
        else Web.setFlashError(req, result == null ? "The refund could not be completed. Please try again." : result.message);
        Web.redirect(req, resp, "/history");
    }
}
