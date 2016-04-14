//package org.cloud.monster.servlet;
//
//import org.cloud.monster.util.DateUtil;
//import org.cloud.monster.util.Decrypt;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Properties;
//
///**
// * This the Q1 Servlet Class of this phase.
// */
//public class HeartBeatAndAuthServlet extends HttpServlet {
////    private static final String message = "Hello, 15619! This is the team : Game Of Cloud";
//
//    private static String TEAM_ID;
//
//    private static String TEAM_AWS_ACCOUNT_ID;
//
//    static {
//        Properties properties = new Properties();
//        try {
//            properties.load(HeartBeatAndAuthServlet.class.getResourceAsStream("/info.properties"));
//        } catch (IOException io) {
//            System.out.println(io);
//        }
//        TEAM_ID = properties.getProperty("team_id");
//        TEAM_AWS_ACCOUNT_ID = properties.getProperty("team_aws_account_id");
//    }
//
//    @Override
//    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
//            throws ServletException, IOException {
//        PrintWriter writer = resp.getWriter();
//
//        String key = req.getParameter("key");
//        String message = req.getParameter("message");
//        String rst = Decrypt.decrypt(key, message);
//        writer.write(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n");
//        writer.write(DateUtil.currentTime() + "\n");
//        writer.write(rst + "\n");
//        writer.close();
//    }
//
//    @Override
//    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
//            throws ServletException, IOException {
//        doGet(req, resp);
//    }
//}
