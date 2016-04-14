package org.cloud.monster.server;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.cloud.monster.servlet.CoordinatorServlet;
import org.xnio.Options;
import javax.servlet.ServletException;
import static io.undertow.servlet.Servlets.*;

/**
 * Coordinator Server.
 * @author Peixin Lu
 */
public class CoordinatorNewServer {

    private static final String PATH = "/";

    public static void main(String[] args) throws Exception {
        try {
            DeploymentInfo servletBuilder = deployment()
                    .setClassLoader(CoordinatorNewServer.class.getClassLoader())
                    .setContextPath(PATH)
                    .setDeploymentName("coodinator.war")
                    .addServlet(
                            servlet("Coordinator", CoordinatorServlet.class)
                                    .addMapping("/")
                    );

            DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
            manager.deploy();

            HttpHandler servletHandler = manager.start();
            PathHandler path = Handlers.path(Handlers.redirect(PATH))
                    .addPrefixPath(PATH, servletHandler);

            Undertow server = Undertow.builder()
                    .addHttpListener(80, "0.0.0.0")
                    .setWorkerOption(Options.WORKER_TASK_MAX_THREADS, 500)
                    .setWorkerOption(Options.WORKER_TASK_CORE_THREADS, 500)
                    .setWorkerOption(Options.WORKER_IO_THREADS, 500)
                    .setHandler(path)
                    .build();

            server.start();

        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
