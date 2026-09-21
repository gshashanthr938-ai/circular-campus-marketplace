package com.campusmarket.web;

import com.campusmarket.dao.ListingDao;
import com.campusmarket.model.Listing;
import com.campusmarket.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import com.campusmarket.service.ListingPolicyService;
import com.campusmarket.service.ListingImageService;
import java.util.List;

/** Listings module: create a new listing or edit an existing one (owner only). */
@WebServlet("/sell")
@MultipartConfig(maxFileSize=3145728,maxRequestSize=10485760,fileSizeThreshold=262144)
public class SellServlet extends HttpServlet {

    private final ListingDao listingDao = new ListingDao();
    private final ListingPolicyService policy = new ListingPolicyService();
    private final ListingImageService imageService = new ListingImageService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }
        long id = parseId(req.getParameter("id"));
        if (id > 0) {
            Listing listing = listingDao.findById(id);
            if (listing == null || listing.getSellerId() != me.getId() || !listing.isAvailable()) {
                req.setAttribute("message", "You can only edit your own listings.");
                Web.render(req, resp, "message.jsp");
                return;
            }
            req.setAttribute("listing", listing);
            req.setAttribute("listingImages",new com.campusmarket.dao.ListingImageDao().forListing(id));
        }
        Web.render(req, resp, "sell.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Student me = Web.currentStudent(req);
        if (me == null) {
            Web.redirect(req, resp, "/login");
            return;
        }

        long id = parseId(req.getParameter("id"));
        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        String category = trim(req.getParameter("category"));
        String condition = trim(req.getParameter("condition"));
        BigDecimal price = parsePrice(req.getParameter("price"));
        List<Part> photos;
        try { photos=imageService.selected(req.getParts()); }
        catch(Exception e){req.setAttribute("error",e.getMessage());req.setAttribute("listing",rebuild(id,title,description,category,price,condition,me));Web.render(req,resp,"sell.jsp");return;}

        if (title == null || title.isEmpty() || category == null || category.isEmpty()
                || condition == null || condition.isEmpty() || price == null
                || price.signum() < 0 || price.scale() > 2 || price.compareTo(new BigDecimal("999999.99")) > 0
                || title.length()>150 || category.length()>50 || (description!=null && description.length()>1000)
                || !java.util.List.of("Like New","Good","Fair").contains(condition)) {
            req.setAttribute("error", "Please fill every field with a valid, non-negative price.");
            req.setAttribute("listing", rebuild(id, title, description, category, price, condition, me));
            Web.render(req, resp, "sell.jsp");
            return;
        }
        if(id<=0&&photos.isEmpty()){
            req.setAttribute("error","Upload at least one clear product picture (maximum three).");
            req.setAttribute("listing",rebuild(id,title,description,category,price,condition,me));Web.render(req,resp,"sell.jsp");return;
        }

        ListingPolicyService.Decision decision = policy.evaluate(title, description, category, price, condition);
        if (!decision.allowed()) {
            req.setAttribute("error", decision.message());
            req.setAttribute("listing", rebuild(id, title, description, category, price, condition, me));
            Web.render(req, resp, "sell.jsp");
            return;
        }

        long savedId=id;
        try {
            if (id > 0) {
                listingDao.update(id, me.getId(), title, description, category, price, condition);
                if(!photos.isEmpty())imageService.save(id,photos,getServletContext());
                Web.setFlash(req, "Listing updated.");
            } else {
                savedId=listingDao.create(me.getId(), title, description, category, price, condition);
                imageService.save(savedId,photos,getServletContext());
                Web.setFlash(req, "Listing published with "+photos.size()+" product picture(s)!");
            }
        } catch(Exception e) {
            if(id<=0&&savedId>0)listingDao.delete(savedId,me.getId());
            req.setAttribute("error",e.getMessage()==null?"The pictures could not be saved.":e.getMessage());
            req.setAttribute("listing",rebuild(id,title,description,category,price,condition,me));Web.render(req,resp,"sell.jsp");return;
        }
        Web.redirect(req, resp, "/my-listings");
    }

    private Listing rebuild(long id, String title, String desc, String cat,
                            BigDecimal price, String cond, Student me) {
        Listing l = new Listing();
        l.setId(id);
        l.setSellerId(me.getId());
        l.setTitle(title);
        l.setDescription(desc);
        l.setCategory(cat);
        l.setPrice(price);
        l.setCondition(cond);
        return l;
    }

    private long parseId(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
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

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
