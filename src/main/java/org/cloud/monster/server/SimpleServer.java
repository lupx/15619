package org.cloud.monster.server;

import io.undertow.Undertow;
import io.undertow.examples.UndertowExample;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.cloud.monster.cache.LRUCache;
import org.cloud.monster.dataaccess.hbase.HBaseDao;
import org.cloud.monster.dataaccess.mysql.TwitterDao;
import org.cloud.monster.pojo.Twitter;
import org.cloud.monster.util.DateUtil;
import org.cloud.monster.util.Decrypt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Simply runs this code by [mvn] to deploy servers for tests.
 *
 * @author PeixinLu
 */
@UndertowExample("Hello World")
public class SimpleServer {
    private static final String TEAM_ID;

    private static final String TEAM_AWS_ACCOUNT_ID;

    /**
     * input the EC2-server's dns into info.properties.
     */
    private static final String DNS;


    private static final BigInteger secretKey;

    /**
     * TEST_TYPEs: q1, q2mysql, q2hbase
     */
    private static final String TEST_TYPE;


    /**
     * CACHE.
     */
//    private static LRUCache<String, String> cache;

    /**
     * TEST: only one dao here.
     * Further, we could provide a array of daos.
     */
    private static TwitterDao twitterDao;

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
        DNS = properties.getProperty("dns");
        twitterDao = new TwitterDao("jdbc:mysql://localhost/twitter");
        TEST_TYPE = properties.getProperty("testType");

        // cache size = 5000
//        cache = new LRUCache<>(5000);
    }

    public static void main(final String[] args) {
        if (TEST_TYPE.equals("q1")) {
            /**
             * q1 server:
             */
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
                    }}).build();
            server.start();
        }

        if (TEST_TYPE.equals("q2mysql")) {
            /**
             * q2 server for MySQL:
             */
            Undertow server = Undertow.builder()
                .addHttpListener(80, DNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        Map<String, Deque<String>> params = exchange.getQueryParameters();
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        String userId = params.get("userid").getFirst();
                        String hashTag = params.get("hashtag").getFirst();
//                        String cachekey = userId + "#" + hashTag;
//
//                        if (cache.containsKey(cachekey)) {
//                            exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//                                    + cache.get(cachekey) + "\n" + "\n");
//                            return;
//                        }
                        List<Twitter> list = null;
                        try {
                            list = twitterDao.retrieveTwitter(userId, hashTag);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e);
                        }
                        String result = buildResponse(list);
//                        cache.put(cachekey, result);
                        exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
                                + result + "\n" + "\n");
                    }
                }).build();
            server.start();
        }

        if (TEST_TYPE.equals("q2hbase")) {
            /**
             * q2 server for HBase:
             */
            Undertow server = Undertow.builder()
                    .addHttpListener(80, DNS)
                    .setHandler(new HttpHandler() {
                        @Override
                        public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        Map<String, Deque<String>> params = exchange.getQueryParameters();
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        String userId = params.get("userid").getFirst();
                        String hashTag = params.get("hashtag").getFirst();
//                        String cachekey = userId + "#" + hashTag;
//                        if (cache.containsKey(cachekey)) {
//                            exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//                                    + cache.get(cachekey) + "\n" + "\n");
//                            return;
//                        }
                        List<Twitter> list = null;
                        try {
                            list = HBaseDao.retrieveTweets(userId + "#" + hashTag);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e);
                        }
                        String result = buildResponse(list);
//                        cache.put(cachekey, result);
                        exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
                                + result + "\n" + "\n");
                        }
                    }).build();
            server.start();
        }
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

    private static String buildResponse(List<Twitter> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i ++) {
            if (i != list.size() - 1) {
                sb.append(list.get(i).toString()).append("\n");
            } else {
                sb.append(list.get(i));
            }
        }
        return sb.toString();
    }
}
