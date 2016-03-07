package org.cloud.monster.server;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.examples.UndertowExample;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.cloud.monster.servlet.HeartBeatAndAuthServlet;
import org.cloud.monster.servlet.HelloServlet;

import javax.servlet.ServletException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;

/**
 * The main Servlet Server.
 * We will use this class to start our Undertow server, or to shut it down.
 * @author PeixinLu
 */
@UndertowExample("Servlet")
public class ServletServer {

    private static Undertow server = null;

    public static final String MYAPP = "/monster";

    public static final String DNS = "ec2-54-165-232-72.compute-1.amazonaws.com";

    public static void main(final String[] args) {
        /** the arg is "start", then start the server **/
//        if (args[0].equals("start")) {
            try {
                DeploymentInfo servletBuilder = deployment()
                        .setClassLoader(ServletServer.class.getClassLoader())
                        .setContextPath("")
                        .setDeploymentName("monster.war")
                        .addServlets(
                                servlet("HelloServlet", HelloServlet.class)
                                        .addMapping("/*"), // to access this, use :  http://localhost:8080/monster
                                servlet("HeartBeatAndAuthServlet", HeartBeatAndAuthServlet.class)
                                        .addMapping("/q1")  // q1 request endpoint
                        );

                DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
                manager.deploy();

                PathHandler path = Handlers.path(Handlers.redirect(""))
                        .addPrefixPath("/", manager.start());

                server = Undertow.builder()
                        .addHttpListener(80, DNS) // listen to port 80
                        .setHandler(path)
                        .build();
                server.start();
                System.out.println("Server is up ... ");

            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
//        } else {
//            // arg is "shutdown"
//
//        }
    }
}
