package com.campusmarket.web;

import com.campusmarket.dao.ListingDao;
import com.campusmarket.dao.WaitlistDao;
import com.campusmarket.model.Listing;
import com.campusmarket.model.Student;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.Set;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private static final Set<String> STATES=Set.of("AVAILABLE","SOLD","REMOVED");
    private final ListingDao listings=new ListingDao();
    private final WaitlistDao waitlist=new WaitlistDao();

    protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {
        if(!admin(req,resp))return;
        req.setAttribute("listings",listings.findAll());
        java.util.LinkedHashSet<String> images=new java.util.LinkedHashSet<>(com.campusmarket.db.Catalog.imageOptions());
        images.addAll(new com.campusmarket.dao.ListingImageDao().allPaths());
        req.setAttribute("imageOptions",java.util.List.copyOf(images));
        Web.consumeFlash(req);Web.render(req,resp,"admin.jsp");
    }
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException {
        if(!admin(req,resp))return;
        try {
            long id=Long.parseLong(req.getParameter("id"));String image=req.getParameter("imagePath");
            String state=req.getParameter("status"),note=req.getParameter("note");
            Listing before=listings.findById(id);
            if(before==null||!STATES.contains(state)||note==null||note.length()>500||!safeImage(image))throw new IllegalArgumentException();
            listings.moderate(id,image,state,note.trim());
            if(!before.isAvailable()&&"AVAILABLE".equals(state))waitlist.notifyAvailable(id,before.getTitle());
            Web.setFlash(req,"Listing moderation saved.");
        } catch(Exception e){Web.setFlash(req,"Admin change rejected. Select a valid local image and status.");}
        Web.redirect(req,resp,"/admin");
    }
    private boolean admin(HttpServletRequest req,HttpServletResponse resp)throws IOException {
        Student me=Web.currentStudent(req);if(me==null||!me.isAdmin()){resp.sendError(403,"Administrator access required.");return false;}return true;
    }
    private boolean safeImage(String image) {
        if(image==null||!image.matches("[a-zA-Z0-9_./-]+\\.(jpg|jpeg|png|webp|svg)"))return false;
        String root=getServletContext().getRealPath("/img");if(root==null)return false;
        try{File base=new File(root).getCanonicalFile(),file=new File(base,image).getCanonicalFile();return file.toPath().startsWith(base.toPath())&&file.isFile();}
        catch(IOException e){return false;}
    }
}
