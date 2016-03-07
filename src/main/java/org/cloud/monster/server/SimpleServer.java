package org.cloud.monster.server;

import io.undertow.Undertow;
import io.undertow.examples.UndertowExample;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.cloud.monster.util.DateUtil;
import org.cloud.monster.util.Decrypt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Simply run this code, then use browser to test it!
 * @author PeixinLu
 */
@UndertowExample("Hello World")
public class SimpleServer {
    private static final String TEAM_ID;

    private static final String TEAM_AWS_ACCOUNT_ID;

    private static final String DNS;

    private static final BigInteger secretKey;


    static {
        Properties properties = new Properties();
        try {
            properties.load(SimpleServer.class.getResourceAsStream("/info.properties"));
        } catch (IOException io) {
            System.out.println(io);
        }
        TEAM_ID = properties.getProperty("team_id");
        TEAM_AWS_ACCOUNT_ID = properties.getProperty("team_aws_account_id");
        secretKey = new BigInteger(properties.getProperty("secret"));
        DNS = "ec2-54-172-11-79.compute-1.amazonaws.com";
//        DNS = "localhost";
    }

    public static void main(final String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(80, DNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        Map<String, Deque<String>> params = exchange.getQueryParameters();
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        String key = params.get("key").getFirst();
                        String message = params.get("message").getFirst();
                        String rst = decrypt(key, message);
                        exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
                                + DateUtil.currentTime() + "\n" + rst + "\n");
                    }
                }).build();
        server.start();
    }

    public static String decrypt(String keyParameter, String message) {
        if (message == null) return null;
        int keyZ = getKeyZ(keyParameter);
//        System.out.println("keyZ = " + keyZ);
        char[] charArray = getCharArray(message, keyZ);
        return getDecryptedMessage(charArray);
    }

    // step1 : Input calculate key Z : GCD
    /**
     * Generates the key Z from the parameter : key
     * @param keyParameter
     * @return keyZ
     */
    static int getKeyZ (String keyParameter) {
        BigInteger keyY = new BigInteger(keyParameter);
        BigInteger gcd = secretKey.gcd(keyY);
        return 1 + gcd.mod(new BigInteger("25")).intValue();
    }

    /**
     *
     * @param messageParameter
     * @param keyZ
     * @return charArray of the decrypted char values but not correct order
     */
    // step2 : Caesarify get intermediate message with key Z
    static char[] getCharArray(String messageParameter, int keyZ) {
        char[] charArray = messageParameter.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] - 'A' >= keyZ) {
                charArray[i] = (char)(charArray[i] - keyZ);
//                System.out.println("new char " + charArray[i]);
            } else {
                int gap = charArray[i] - 'A';
//                System.out.println("gap = " + gap);
                charArray[i] = (char)('Z' - (keyZ-gap) + 1);
            }
        }
        return charArray;
    }

    /**
     *
     * @param charArray with decrypted char values but not correct order
     * @return String decrypted message
     */
    // step3 : Spiralize get decrypted message from the matrix
    static String getDecryptedMessage(char[] charArray) {
        StringBuilder sb = new StringBuilder();
        int len = (int) Math.sqrt(charArray.length);
//        System.out.println("matrix length = " + len);
        // initialize matrix with values from charArray
        char[][] matrix = new char[len][len];
        for(int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                matrix[i][j] = charArray[i * len + j];
            }
        }
        int currentLen = len;
        while(currentLen > 0) {
//            System.out.println(currentLen);
            int start = (len - currentLen) / 2;
//            System.out.println("start = " + start);
            int end = start + currentLen - 1;
//            System.out.println("end = " + end);
            for (int i = 0; i < currentLen; i++) {
                sb.append(matrix[start][start + i]);
            }
            for (int i = 1; i < currentLen; i++) {
                sb.append(matrix[start + i][end]);
            }
            for (int i = 1; i < currentLen; i++) {
                sb.append(matrix[end][end - i]);
            }
            for (int i = 1; i < currentLen - 1; i++) {
                sb.append(matrix[end - i][start]);
            }
            currentLen = currentLen - 2;
        }
        return sb.toString();

    }

}
