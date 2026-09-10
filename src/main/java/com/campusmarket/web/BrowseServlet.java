package com.campusmarket.web;

import com.campusmarket.dao.ListingDao;
import com.campusmarket.model.Listing;
import com.campusmarket.util.CookieUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Browse & Search module. Guest-accessible; shows a cookie-based "recently viewed" strip. */
@WebServlet("/browse")
public class BrowseServlet extends HttpServlet {

    private final ListingDao listingDao = new ListingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String search = req.getParameter("search");
        String category = req.getParameter("category");
        BigDecimal maxPrice = parsePrice(req.getParameter("maxPrice"));

        List<Listing> listings = listingDao.search(search, category, maxPrice);

        // Recently-viewed items come from a cookie (works for guests too).
        List<Listing> recentlyViewed = new ArrayList<>();
        for (Long id : CookieUtil.parseIds(CookieUtil.get(req, CookieUtil.RECENTLY_VIEWED))) {
            Listing l = listingDao.findById(id);
            if (l != null) {
                recentlyViewed.add(l);
            }
        }

        Web.consumeFlash(req);
        req.setAttribute("listings", listings);
        req.setAttribute("categories", listingDao.distinctCategories());
        req.setAttribute("recentlyViewed", recentlyViewed);
        req.setAttribute("search", search);
        req.setAttribute("selectedCategory", category);
        req.setAttribute("maxPrice", req.getParameter("maxPrice"));
        Web.render(req, resp, "browse.jsp");
    }

    private BigDecimal parsePrice(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
