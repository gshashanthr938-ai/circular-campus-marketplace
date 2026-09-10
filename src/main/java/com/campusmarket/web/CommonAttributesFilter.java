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
        String uri = req.getRequestURI();

        if (uri == null || !uri.contains(".")) {
            Student student = Web.currentStudent(req);
            req.setAttribute("currentStudent", student);

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
