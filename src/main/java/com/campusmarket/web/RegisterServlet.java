package com.campusmarket.web;

import com.campusmarket.dao.StudentDao;
import com.campusmarket.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    /** Every new student starts with a demo wallet balance. */
    private static final BigDecimal STARTING_BALANCE = new BigDecimal("2000.00");

    private final StudentDao studentDao = new StudentDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Web.render(req, resp, "register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = trim(req.getParameter("name"));
        String email = trim(req.getParameter("email"));
        String password = req.getParameter("password");

        String error = validate(name, email, password);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            Web.render(req, resp, "register.jsp");
            return;
        }

        long id = studentDao.create(name, email, password, STARTING_BALANCE);
        Student student = studentDao.findById(id);

        // Auto-login, then migrate any guest cart the visitor built before signing up.
        req.getSession().setAttribute("student", student);
        Web.migrateGuestCartToSession(req, resp);

        Web.setFlash(req, "Account created. You have a wallet of " + STARTING_BALANCE + " to start!");
        Web.redirect(req, resp, "/browse");
    }

    private String validate(String name, String email, String password) {
        if (name == null || name.isEmpty()) {
            return "Name is required.";
        }
        if (email == null || !email.contains("@")) {
            return "A valid email is required.";
        }
        if (password == null || password.length() < 4) {
            return "Password must be at least 4 characters.";
        }
        if (studentDao.emailExists(email)) {
            return "That email is already registered. Try logging in.";
        }
        return null;
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
