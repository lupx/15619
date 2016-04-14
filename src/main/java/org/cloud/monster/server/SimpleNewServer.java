package org.cloud.monster.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.cloud.monster.cache.LRUCache;
import org.cloud.monster.dataaccess.mysql.HikariDao;
import org.cloud.monster.dataaccess.mysql.TwitterDao;
import org.cloud.monster.util.MD5Util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//import io.undertow.examples.UndertowExample;
//import org.cloud.monster.dataaccess.hbase.HBaseAsyncDao;
//import org.cloud.monster.dataaccess.hbase.HBaseDao;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;

/**
 * Simply runs this code by [mvn] to deploy servers for tests.
 *
 * @author PeixinLu
 */
//@UndertowExample("Hello World")
public class SimpleNewServer {

    private static final String TEAM_ID;

    private static final String TEAM_AWS_ACCOUNT_ID;

    /**
     * input the EC2-server's dns into info.properties.
     */
    private static final String DNS;

    private static final BigInteger secretKey;

    /**
     * CACHE.
     */
    private static LRUCache<String, String> cache;

    /**
     * TEST: only one dao here.
     * Further, we could provide a array of daos.
     */
    private static TwitterDao twitterDao;

    //private static Executor executor;

    static {
        Properties properties = new Properties();
        try {
            properties.load(SimpleNewServer.class.getResourceAsStream("/info.properties"));
        } catch (IOException io) {
            System.out.println(io);
        }
        TEAM_ID = properties.getProperty("team_id");
        TEAM_AWS_ACCOUNT_ID = properties.getProperty("team_aws_account_id");
        secretKey = new BigInteger(properties.getProperty("secret"));
        DNS = properties.getProperty("dns");
//        try {
//            twitterDao = new TwitterDao("jdbc:mysql://localhost/twitter");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Connections get failed.");
//        }
        // cache size = 5000
        //cache = new LRUCache<String, String>(1000000);

        //executor = Executors.newFixedThreadPool(6);
    }



    public static void main(final String[] args) {

//        if (SERVER_TYPE.equals("mysql")) {
            Undertow server = Undertow.builder()
                    .addHttpListener(80, DNS)
                    .setHandler(new HttpHandler() {
                        
                        ConcurrentHashMap<String, PriorityQueue<Integer>> KVMap = new ConcurrentHashMap<>();
                        @Override
                        public void handleRequest(final HttpServerExchange exchange) throws Exception {
                            if (exchange.getRelativePath().equals("/test")) {
//                                KVMap.putIfAbsent(tweetid, new PriorityQueue<Integer>());
//                                synchronized (KVMap.get(tweetid)) {
//                                    KVMap.get(tweetid).offer(seq);
//                                }
                                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                Map<String, Deque<String>> params = exchange.getQueryParameters();
                                final int testid = Integer.parseInt(params.get("testid").getFirst());
                                if (testid == 1) {
                                    Thread.sleep(20000);
                                    exchange.getResponseSender().send("Slept 20s! The testid is:" + testid);
                                } else {
                                    exchange.getResponseSender().send("success! The testid is:" + testid);
                                }


//                                final String tweetid = params.get("tweetid").getFirst();
//                                final String[] fields = params.get("fields").getFirst().split(",");
//                                final String[] payload = params.get("payload").getFirst().split(",");
//                                final String op = params.get("op").getFirst();
//                                        synchronized (KVMap.get(tweetid)) {
//                                            while (seq != KVMap.get(tweetid).peek()) {
//                                                KVMap.get(tweetid).wait();
//                                            }
//                                        }
//                                exchange.dispatch(new HttpHandler() {
//                                    @Override
//                                    public void handleRequest(HttpServerExchange exchange) throws Exception {
//                                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//
//                                        if (op.equals("set")) {
//                                            exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//                                                    + "success" + "\n");
//                                            HikariDao.updateRecord(tweetid, fields, payload);
//                                        }
//
//                                        if (op.equals("get")) {
//                                            String result = HikariDao.getRecord(tweetid, fields[0]);
//                                            result = result.replaceAll(" ", "+");
//                                            exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//                                                    + result + "\n");
//                                        }
//                                    }
//                                });
//                                synchronized (KVMap.get(tweetid)) {
//                                    KVMap.get(tweetid).poll();
//                                    KVMap.get(tweetid).notifyAll();
//                                }
                            }
                        }
                    }).build();
            server.start();
//        }

//        if (SERVER_TYPE.equals("hbase")) {
//            Undertow server = Undertow.builder()
//                    .addHttpListener(80, DNS)
//                    .setHandler(new HttpHandler() {
//                        @Override
//                        public void handleRequest(final HttpServerExchange exchange) throws Exception {
//                            if (exchange.getRelativePath().equals("/q1")) {
//                                // q1 test
//                                exchange.dispatch(new HttpHandler() {
//                                    @Override
//                                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
//                                        Map<String, Deque<String>> params = exchange.getQueryParameters();
//                                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//                                        String key = params.get("key").getFirst();
//                                        String message = params.get("message").getFirst();
//                                        String rst = decrypt(key, message);
//                                        exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//                                                + DateUtil.currentTime() + "\n" + rst + "\n");
//                                    }
//                                });
//                            } else if (exchange.getRelativePath().equals("/q2")) {
//                                // q2 hbase handler
//                                exchange.dispatch(new HttpHandler() {
//                                    @Override
//                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
//                                        Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
//                                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//                                        String userId = params.get("userid").getFirst();
//                                        String hashTag = params.get("hashtag").getFirst();
//                                        String key = userId + hashTag;
//    //                        if (cache.containsKey(key)) {
//    //                            exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//    //                                    + cache.get(key) + "\n");
//    //                            return;
//    //                        }
//                                        String result = null;
//                                        try {
//                                            result = HBaseAsyncDao.retrieveTweets(MD5Util.getMD5(key));
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            System.out.println(e);
//                                        }
//    //                        cache.put(key, result);
//                                        httpServerExchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//                                                + result + "\n");
//                                    }
//                                });
//                            } else {
//                                //q3 hbase handler ...
//                                exchange.dispatch(new HttpHandler() {
//                                    // q3
//                                    @Override
//                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
//                                        Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
//                                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//
//                                        String startDate = params.get("start_date").getFirst().replace("-","");
//                                        String endDate = params.get("end_date").getFirst().replace("-","");
//                                        String startUid = params.get("start_userid").getFirst();
//                                        String endUid = params.get("end_userid").getFirst();
//
//                                        String wordpara = params.get("words").getFirst();
//                                        String[] words = wordpara.split(",");
//
//                                        Map<String, Integer> wordmap = new HashMap<>();
//
//                                        wordmap.put(words[0], 0);
//                                        wordmap.put(words[1], 0);
//                                        wordmap.put(words[2], 0);
//
//                                        StringBuilder sb = new StringBuilder();
//                                        if (startUid.length() != 10) {
//                                            for (int i = 0; i < 10 - startUid.length(); i++) {
//                                                sb.append("0");
//                                            }
//                                        }
//                                        String startKey = sb.toString() + startUid + startDate;
//
//                                        sb = new StringBuilder();
//                                        if (endUid.length() != 10) {
//                                            for (int i = 0; i < 10 - endUid.length(); i++) {
//                                                sb.append("0");
//                                            }
//                                        }
//                                        String endKey = sb.toString() + endUid + endDate;
//
//            //                        if (cache.containsKey(key)) {
//            //                            exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
//            //                                    + cache.get(key) + "\n" + "\n");
//            //                            return;
//            //                        }
//                                        List<String> result = null;
//                                        try {
//                                            result = HBaseAsyncDao.retrieveRange(startKey, endKey, startDate, endDate);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            System.out.println(e);
//                                        }
//
//                                        for (String str: result) {
//                                            if (!str.contains(words[0]) && !str.contains(words[1]) && !str.contains(words[2])) {
//                                                continue;
//                                            }
//                                            String[] parts = str.split("\\|");
//                                            for (int i = 0; i < parts.length; i++) {
//                                                String[] wordcount = parts[i].split(":");
//                                                String word = wordcount[0];
//                                                if (wordmap.containsKey(word)) {
//                                                    wordmap.put(word, wordmap.get(word) + Integer.parseInt(wordcount[1]));
//                                                }
//                                            }
//                                        }
//
//            //                        cache.put(key, result);
//
//                                        StringBuilder stringBuilder = new StringBuilder();
//                                        stringBuilder.append(TEAM_ID+","+TEAM_AWS_ACCOUNT_ID+"\n"
//                                                + words[0] + ":" + wordmap.get(words[0]) + "\n"
//                                                + words[1] + ":" + wordmap.get(words[1]) + "\n"
//                                                + words[2] + ":" + wordmap.get(words[2]) + "\n");
//                                        httpServerExchange.getResponseSender().send(stringBuilder.toString());
//                                    }
//                                });
//                            }
//                        }
//                    }).build();
//            server.start();
//        }
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

    private static String buildResponse(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                sb.append(list.get(i)).append("\n");
            } else {
                sb.append(list.get(i));
            }
        }
        return sb.toString();
    }
}
