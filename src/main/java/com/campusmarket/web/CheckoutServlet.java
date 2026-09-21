package com.campusmarket.web;

import com.campusmarket.model.Student;
import com.campusmarket.service.CheckoutService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Checkout module: confirms a demo UPI/net-banking payment and creates transactions. */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CheckoutService checkoutService = new CheckoutService();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }

        String method=req.getParameter("paymentMethod");
        String detail="UPI".equals(method)?req.getParameter("upiId"):req.getParameter("bankCode");
        boolean termsAccepted="yes".equals(req.getParameter("acceptTerms"));
        CheckoutService.Result result = checkoutService.checkout(me.getId(), Web.cartSid(req),method,detail,termsAccepted);

        Web.setFlash(req, result.message);
        Web.redirect(req, resp, result.success ? "/history" : "/cart");
    }
}
