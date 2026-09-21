package com.campusmarket.web;

import com.campusmarket.dao.ReviewDao;
import com.campusmarket.model.Student;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {
    private final ReviewDao dao=new ReviewDao();
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException {
        Student me=Web.currentStudent(req);if(me==null){Web.redirect(req,resp,"/login");return;}
        boolean saved=false;
        try{saved=dao.create(Long.parseLong(req.getParameter("txnId")),me.getId(),Integer.parseInt(req.getParameter("rating")),req.getParameter("comment"));}catch(Exception ignored){}
        Web.setFlash(req,saved?"Thanks—your review is now visible.":"Review could not be saved. Use 1–5 stars and one review per purchase.");
        Web.redirect(req,resp,"/history");
    }
}
