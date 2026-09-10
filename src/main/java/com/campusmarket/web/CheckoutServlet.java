package com.campusmarket.web;

import com.campusmarket.dao.StudentDao;
import com.campusmarket.model.Student;
import com.campusmarket.service.CheckoutService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Checkout module: deducts the wallet and converts the cart into transactions. */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CheckoutService checkoutService = new CheckoutService();
    private final StudentDao studentDao = new StudentDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }

        CheckoutService.Result result = checkoutService.checkout(me.getId(), Web.cartSid(req));

        // Refresh the session copy of the student so the header/wallet reflect the new balance.
        Student refreshed = studentDao.findById(me.getId());
        req.getSession().setAttribute("student", refreshed);

        Web.setFlash(req, result.message);
        Web.redirect(req, resp, result.success ? "/history" : "/cart");
    }
}
