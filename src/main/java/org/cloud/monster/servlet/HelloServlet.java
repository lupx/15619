//package org.cloud.monster.servlet;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
///**
// * Examples to show request and response.
// * @author Peixin Lu
// */
//public class HelloServlet extends HttpServlet {
//
//    private static final String message = "Hello, 15619! This is the team : Game Of Cloud";
//
//    @Override
//    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
//            throws ServletException, IOException {
//        PrintWriter writer = resp.getWriter();
//        writer.write(message);
//        writer.close();
//    }
//
//    @Override
//    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
//            throws ServletException, IOException {
//        doGet(req, resp);
//    }
//
//}
