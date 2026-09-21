package com.campusmarket.web;

import com.campusmarket.dao.StudentDao;
import com.campusmarket.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

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
        String phone = trim(req.getParameter("phone"));
        String password = req.getParameter("password");

        String error = validate(name, email, phone, password);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.setAttribute("phone", phone);
            Web.render(req, resp, "register.jsp");
            return;
        }

        long id = studentDao.create(name, email, phone, password);
        Student student = studentDao.findById(id);

        // Auto-login, then migrate any guest cart the visitor built before signing up.
        req.getSession();
        req.changeSessionId();
        req.getSession().setAttribute("student", student);
        Web.migrateGuestCartToSession(req, resp);

        Web.setFlash(req, "Account created. Choose UPI or net banking when you check out.");
        Web.redirect(req, resp, "/browse");
    }

    private String validate(String name, String email, String phone, String password) {
        if (name == null || name.isEmpty() || name.length() > 100) {
            return "Name is required.";
        }
        if (email == null || email.length()>150 || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            return "A valid email is required.";
        }
        if (phone == null || phone.length() > 20 || !phone.matches("\\+?[0-9][0-9 ()-]{8,18}[0-9]")) {
            return "Enter a valid phone number with 10 to 15 digits.";
        }
        if (password == null || password.length() < 8 || password.length() > 128) {
            return "Password must be between 8 and 128 characters.";
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
