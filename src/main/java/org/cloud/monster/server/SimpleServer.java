package org.cloud.monster.server;

import io.undertow.Undertow;
import io.undertow.examples.UndertowExample;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.cloud.monster.util.DateUtil;
import org.cloud.monster.util.Decrypt;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.Properties;

/**
 * This is a simple server example.
 * Simply run this code, then use browser to test it!
 * @author PeixinLu
 */
@UndertowExample("Hello World")
public class SimpleServer {
    private static final String TEAM_ID;

    private static final String TEAM_AWS_ACCOUNT_ID;

    private static final String DNS;
    static {
        Properties properties = new Properties();
        try {
            properties.load(SimpleServer.class.getResourceAsStream("/info.properties"));
        } catch (IOException io) {
            System.out.println(io);
        }
        TEAM_ID = properties.getProperty("team_id");
        TEAM_AWS_ACCOUNT_ID = properties.getProperty("team_aws_account_id");
//        DNS = "ec2-54-172-11-79.compute-1.amazonaws.com";
        DNS = "localhost";
    }

    public static void main(final String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, DNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        Map<String, Deque<String>> params = exchange.getQueryParameters();
                        String key = params.get("key").poll();
                        String message = params.get("message").poll();
                        String rst = Decrypt.decrypt(key, message);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
                                + DateUtil.currentTime() + "\n" + rst + "\n");
                    }
                }).build();
        server.start();
    }
}
