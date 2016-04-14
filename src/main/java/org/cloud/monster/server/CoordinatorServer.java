package org.cloud.monster.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.proxy.*;
import io.undertow.util.Headers;
import org.cloud.monster.cache.LRUCache;
import org.cloud.monster.dataaccess.mysql.HikariDao;
import org.cloud.monster.dataaccess.mysql.TwitterDao;
import org.cloud.monster.util.DateUtil;
import org.cloud.monster.util.MD5Util;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by PeixinLu on 16/4/10.
 */
public class CoordinatorServer {

    private static final String TEAM_ID;

    private static final String TEAM_AWS_ACCOUNT_ID;
    /**
     * input the EC2-server's dns into info.properties.
     */
    private static final String DNS;

    private static final String[] BackEndServers;

    /**
     * CACHE.
     */
    private static LRUCache<String, String> cache;

    //private static Executor executor;

    static {
//        Properties properties = new Properties();
//        try {
//            properties.load(CoordinatorServer.class.getResourceAsStream("/info.properties"));
//        } catch (IOException io) {
//            System.out.println(io);
//        }
        Properties properties = new Properties();
        try {
            properties.load(SimpleServer.class.getResourceAsStream("/info.properties"));
        } catch (IOException io) {
            System.out.println(io);
        }
        TEAM_ID = properties.getProperty("team_id");
        TEAM_AWS_ACCOUNT_ID = properties.getProperty("team_aws_account_id");
        DNS = "ec2-54-88-96-36.compute-1.amazonaws.com"; // this server's DNS
        BackEndServers = new String[]{
                "http://ec2-54-175-69-31.compute-1.amazonaws.com", // server1
                "", // server2
                "", // server3
                "" // server4
        };

        // cache size = 5000
        //cache = new LRUCache<String, String>(1000000);
    }

    public static void main(final String[] args) {
        SimpleProxyClientProvider simpleProxy1 = null;
        SimpleProxyClientProvider simpleProxy2 = null;
        SimpleProxyClientProvider simpleProxy3 = null;
        SimpleProxyClientProvider simpleProxy4 = null;
        try {
            simpleProxy1 = new SimpleProxyClientProvider(new URI(BackEndServers[0]));
            simpleProxy2 = new SimpleProxyClientProvider(new URI(BackEndServers[1]));
            simpleProxy3 = new SimpleProxyClientProvider(new URI(BackEndServers[2]));
            simpleProxy4 = new SimpleProxyClientProvider(new URI(BackEndServers[3]));
        } catch (URISyntaxException u) {
        }
        ProxyHandler handler1 = new ProxyHandler(simpleProxy1, 50000, ResponseCodeHandler.HANDLE_404);
        ProxyHandler handler2 = new ProxyHandler(simpleProxy2, 50000, ResponseCodeHandler.HANDLE_404);
        ProxyHandler handler3 = new ProxyHandler(simpleProxy3, 50000, ResponseCodeHandler.HANDLE_404);
        ProxyHandler handler4 = new ProxyHandler(simpleProxy4, 50000, ResponseCodeHandler.HANDLE_404);

        Undertow server = Undertow.builder()
                .addHttpListener(80, DNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        if (exchange.getRelativePath().equals("/q1")) {
                            // q1 test
//                            exchange.dispatch(new HttpHandler() {
//                                @Override
//                                public void handleRequest(final HttpServerExchange exchange) throws Exception {
//                                    Map<String, Deque<String>> params = exchange.getQueryParameters();
//                                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//                                    String key = params.get("key").getFirst();
//                                    String message = params.get("message").getFirst();
//                                }
//                            });

//                            exchange.dispatch(new HttpHandler() {
//                                @Override
//                                public void handleRequest(HttpServerExchange exchange) throws Exception {
//                                    exchange.setRelativePath(exchange.getRequestPath()); // need this otherwise proxy forwards to chopped off path
//                                    System.out.println(exchange.getRequestPath());
                                    handler1.handleRequest(exchange);
//                                }
//                            });
                        } else if (exchange.getRelativePath().equals("/q2")) {
                            // q2 test
                            exchange.dispatch(new HttpHandler() {
                                @Override
                                public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

                                }
                            });
                        } else if (exchange.getRelativePath().equals("/q3")) {
                            exchange.dispatch(new HttpHandler() {
                                // q3 test
                                @Override
                                public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                }
                            });
                        } else {
                            if (exchange.isInIoThread()) {
                                exchange.dispatch(this);
                                return;
                            }
//                            exchange.dispatch(new HttpHandler() {
//                                // q4 test
//                                @Override
//                                public void handleRequest(HttpServerExchange exchange) throws Exception {
                                    Map<String, Deque<String>> params = exchange.getQueryParameters();
                                    String tweetId = params.get("tweetid").getFirst();
//                                    String op = params.get("op").getFirst();

//                                    if (op.equals("set")) {
//                                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//                                        exchange.setRelativePath(exchange.getRequestPath());
//                                        handler1.handleRequest(exchange);
//                                        exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//                                                + "success" + "\n");
//                                        return;
//                                    }

//                                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                    exchange.setRelativePath(exchange.getRequestPath());

                                    // md5(tweetid)
                                    String h = MD5Util.getMD5(tweetId);
                                    char c = h.charAt(0);
                                    // hash to back-end
//                                    if (c >= '0' && c <= '3') {
//                                        // need this otherwise proxy forwards to chopped off path
////                                        System.out.println(exchange.getRequestPath());
//                                        handler1.handleRequest(exchange);
//                                    }
//                                    if (c >= '4' && c <= '7') {
////                                        System.out.println(exchange.getRequestPath());
//                                        handler2.handleRequest(exchange);
//                                    }
//                                    if ((c >= '8' && c <= '9') || (c >= 'a' && c <= 'b')) {
////                                        System.out.println(exchange.getRequestPath());
//                                        handler3.handleRequest(exchange);
//                                    }
//                                    if (c >= 'c' && c <= 'f') {
////                                        System.out.println(exchange.getRequestPath());
//                                        handler4.handleRequest(exchange);
//                                    }
                                    handler1.handleRequest(exchange);
//                                }
//                            });
                        }
                    }
                }).build();
        server.start();
    }
}
