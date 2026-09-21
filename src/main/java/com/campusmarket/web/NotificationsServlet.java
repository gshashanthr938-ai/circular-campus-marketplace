package com.campusmarket.web;

import com.campusmarket.dao.NotificationDao;
import com.campusmarket.model.Student;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/notifications")
public class NotificationsServlet extends HttpServlet {
    private final NotificationDao dao=new NotificationDao();
    protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {
        Student me=Web.currentStudent(req);if(me==null){Web.redirect(req,resp,"/login");return;}
        req.setAttribute("notifications",dao.findFor(me.getId()));Web.render(req,resp,"notifications.jsp");
    }
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException {
        Student me=Web.currentStudent(req);if(me!=null)dao.markAllRead(me.getId());Web.redirect(req,resp,"/notifications");
    }
}
