package org.cloud.monster.servlet;

import org.cloud.monster.util.MD5Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Coordinator Servlet for Q4
 * @author Peixin Lu
 */
public class CoordinatorServlet extends HttpServlet {

    private static String[] BACKENDS = {
            "http://",
            "http://",
            "http://",
            "http://"
    };

    private static int COUNT = 0;

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.equals("/q4")) {
            // q4
            String tweetid = request.getParameter("tweetid");

            if (tweetid != null) {
                String h = MD5Util.getMD5(tweetid);
                char c = h.charAt(0);
                if (c >= '0' && c <= '7') {
                    response.sendRedirect(BACKENDS[0] + "/q4?" + request.getQueryString());
                }
                if ((c >= '8' && c <= '9') || (c >='a' && c<='f')) {
                    response.sendRedirect(BACKENDS[1] + "/q4?" + request.getQueryString());
                }
            }

//            response.setHeader("Content-type", "text;charset=UTF-8");
//            response.setContentType("text;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            PrintWriter out = response.getWriter();
//            out.write("AngryBanana, 568049388418\n");
//            out.close();
        } else {
            // q1/2/3 round robin
            COUNT = (++COUNT) % BACKENDS.length;
            response.sendRedirect(BACKENDS[COUNT] + path + "?" + request.getQueryString());
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }


}
