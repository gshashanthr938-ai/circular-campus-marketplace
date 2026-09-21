package com.campusmarket.web;

import com.campusmarket.dao.StudentDao;
import com.campusmarket.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final StudentDao studentDao = new StudentDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (Web.isLoggedIn(req)) {
            Web.redirect(req, resp, "/browse");
            return;
        }
        Web.render(req, resp, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Student student = studentDao.authenticate(email == null ? "" : email.trim(),
                password == null ? "" : password);

        if (student == null) {
            req.setAttribute("error", "Invalid email or password.");
            req.setAttribute("email", email);
            Web.render(req, resp, "login.jsp");
            return;
        }

        // Create the logged-in session (sessions hold identity and cart state).
        req.getSession();
        req.changeSessionId();
        req.getSession().setAttribute("student", student);

        // Core requirement: merge the guest cookie-cart into the session cart.
        Web.migrateGuestCartToSession(req, resp);

        Web.setFlash(req, "Welcome back, " + student.getName() + "!");
        Web.redirect(req, resp, "/browse");
    }
}
