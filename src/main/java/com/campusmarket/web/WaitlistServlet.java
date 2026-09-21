package com.campusmarket.web;

import com.campusmarket.dao.WaitlistDao;
import com.campusmarket.model.Student;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/waitlist")
public class WaitlistServlet extends HttpServlet {
    private final WaitlistDao dao=new WaitlistDao();
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException {
        Student me=Web.currentStudent(req);if(me==null){Web.redirect(req,resp,"/login");return;}
        try { long id=Long.parseLong(req.getParameter("id"));dao.join(id,me.getId());Web.setFlash(req,"You are on the waitlist. We will notify you here if it becomes available."); }
        catch(Exception e){Web.setFlash(req,"Could not join that waitlist.");}
        Web.redirect(req,resp,"/listing?id="+req.getParameter("id"));
    }
}
