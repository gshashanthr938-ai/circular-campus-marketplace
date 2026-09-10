package com.campusmarket;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Launches the app on an EMBEDDED Apache Tomcat 9 server, so there is no
 * separate Tomcat to download or configure. Run with:  mvn compile exec:java
 * then open http://localhost:8080/
 */
public class Main {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(
                System.getProperty("port", System.getenv().getOrDefault("PORT", "8080")));

        String webappDir = new File("src/main/webapp").getAbsolutePath();
        String classesDir = new File("target/classes").getAbsolutePath();

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(new File("target/tomcat").getAbsolutePath());
        tomcat.setPort(port);
        tomcat.getConnector(); // create the default HTTP connector

        Context ctx = tomcat.addWebapp("", webappDir);

        // Make the webapp delegate to the classloader that loaded this app (and
        // the embedded Tomcat jars). Without this, parsing web.xml fails to find
        // Tomcat's own descriptor classes under Maven exec:java.
        ctx.setParentClassLoader(Main.class.getClassLoader());

        // Expose compiled classes as /WEB-INF/classes so @WebServlet / @WebFilter
        // / @WebListener annotations are scanned when running from Maven.
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(
                new DirResourceSet(resources, "/WEB-INF/classes", classesDir, "/"));
        ctx.setResources(resources);

        tomcat.start();
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  Circular Campus Marketplace is running!");
        System.out.println("  Open:  http://localhost:" + port + "/");
        System.out.println("  Stop:  press Ctrl+C in this window");
        System.out.println("==================================================");
        System.out.println();
        tomcat.getServer().await();
    }
}
