package com.campusmarket.web;

import com.campusmarket.dao.CartDao;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private final CartDao cartDao = new CartDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            // Tidy up this session's cart rows, then invalidate the session
            // (demonstrates session invalidation on logout).
            cartDao.clear(session.getId());
            session.invalidate();
        }
        Web.redirect(req, resp, "/browse");
    }
}
