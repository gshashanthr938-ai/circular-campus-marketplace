package com.campusmarket.web;

import com.campusmarket.dao.CartDao;
import com.campusmarket.model.Student;
import com.campusmarket.util.CookieUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Runs on every page request and exposes two things the shared header needs:
 *   - currentStudent : the logged-in user (or null)
 *   - cartCount      : number of items in the cart (session cart if logged in,
 *                      guest cookie cart otherwise)
 * Static assets (anything with a '.') are skipped.
 */
@WebFilter("/*")
public class CommonAttributesFilter implements Filter {

    private final CartDao cartDao = new CartDao();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        javax.servlet.http.HttpServletResponse resp=(javax.servlet.http.HttpServletResponse)response;
        req.setCharacterEncoding("UTF-8");
        resp.setHeader("X-Content-Type-Options","nosniff");
        resp.setHeader("X-Frame-Options","DENY");
        if (!req.getRequestURI().contains(".")) {
            javax.servlet.http.HttpSession session=req.getSession();
            String token=(String)session.getAttribute("csrfToken");
            if(token==null) { token=java.util.UUID.randomUUID().toString(); session.setAttribute("csrfToken",token); }
            req.setAttribute("csrfToken",token);
            if("POST".equals(req.getMethod()) && !token.equals(req.getParameter("csrfToken"))) {
                resp.sendError(403,"Please reload the page before submitting the form."); return;
            }
        }
        String uri = req.getRequestURI();

        if (uri == null || !uri.contains(".")) {
            Student student = Web.currentStudent(req);
            if(student!=null) {
                student=new com.campusmarket.dao.StudentDao().findById(student.getId());
                req.getSession().setAttribute("student",student);
            }
            req.setAttribute("currentStudent", student);
            if(student!=null) req.setAttribute("notificationCount",new com.campusmarket.dao.NotificationDao().unreadCount(student.getId()));

            int cartCount;
            if (student != null) {
                cartCount = cartDao.count(req.getSession().getId());
            } else {
                cartCount = CookieUtil.parseIds(CookieUtil.get(req, CookieUtil.GUEST_CART)).size();
            }
            req.setAttribute("cartCount", cartCount);
        }
        chain.doFilter(request, response);
    }
}
