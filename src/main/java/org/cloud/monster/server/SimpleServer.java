package org.cloud.monster.server;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.undertow.Undertow;
//import io.undertow.examples.UndertowExample;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.cloud.monster.cache.LRUCache;
//import org.cloud.monster.dataaccess.hbase.HBaseAsyncDao;
//import org.cloud.monster.dataaccess.hbase.HBaseDao;
import org.cloud.monster.dataaccess.mysql.HikariDao;
import org.cloud.monster.dataaccess.mysql.TwitterDao;
import org.cloud.monster.pojo.Record;
import org.cloud.monster.pojo.Twitter;
import org.cloud.monster.util.DateUtil;
import org.cloud.monster.util.Decrypt;
import org.cloud.monster.util.MD5Util;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.ipfilter.OneIpFilterHandler;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;

/**
 * Simply runs this code by [mvn] to deploy servers for tests.
 *
 * @author PeixinLu
 */
//@UndertowExample("Hello World")
public class SimpleServer {

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
    private static LRUCache<String, Record> cache;

    private static Map<String, Record> batch;


    /**
     * TEST: only one dao here.
     * Further, we could provide a array of daos.
     */
    private static TwitterDao twitterDao;

    //private static Executor executor;

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
//        try {
//            twitterDao = new TwitterDao("jdbc:mysql://localhost/twitter");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Connections get failed.");
//        }
        // cache size = 5000000
        //cache = new LRUCache<String, Record>(200000);

        //batch = new ConcurrentHashMap<String, Record>(1000); //initially 1000

        //executor = Executors.newFixedThreadPool(6);
    }

    private static final HashMap<String, Object> syncMap = new HashMap<>();

    private static final HashMap<String, Integer> latestSeqs = new HashMap<>();

    public static void main(final String[] args) {
	ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
//        if (SERVER_TYPE.equals("mysql")) {
        Undertow server = Undertow.builder()
                .addHttpListener(80, DNS)
                .setHandler(new HttpHandler() {

                    //ConcurrentHashMap<String, PriorityQueue<Integer>> KVMap = new ConcurrentHashMap<>();

                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        if (exchange.getRelativePath().equals("/q1")) {
                            // q1 test
                            if (exchange.isInIoThread()) {
                                exchange.dispatch(this);
                                System.out.println("dispatch to myself!");
                                return;
                            }

                           exchange.dispatch(new HttpHandler() {
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
                           });
                        } else if (exchange.getRelativePath().equals("/q2")) {
                            // q2 test
                            exchange.dispatch(new HttpHandler() {
                                @Override
                                public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                    Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
                                    httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                    String userId = params.get("userid").getFirst();
                                    String hashTag = params.get("hashtag").getFirst();
                                    String key = userId + hashTag;
        //                        if (cache.containsKey(key)) {
        //                            exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
        //                                    + cache.get(key) + "\n" + "\n");
        //                            return;
        //                        }
                                    List<String> list = null;
                                    try {
                                        list = HikariDao.retrieveTwitter(MD5Util.getMD5(key));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        System.out.println(e);
                                    }
                                    String result = buildResponse(list);
        //                        cache.put(key, result);
                                    httpServerExchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
                                            + result + "\n");
                                }
                            });
                        } else if (exchange.getRelativePath().equals("/q3")) {
                            exchange.dispatch(new HttpHandler() {
                                // q3 test
                                @Override
                                public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                    Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
                                    httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");


                                    String start_date = params.get("start_date").getFirst().replace("-","");
                                    String end_date = params.get("end_date").getFirst().replace("-","");
                                    String start_userid = params.get("start_userid").getFirst();
                                    String end_userid = params.get("end_userid").getFirst();

                                    String wordpara = params.get("words").getFirst();
                                    String[] words = wordpara.split(",");
                                    Map<String, Integer> wordmap = new HashMap<>();
                                    wordmap.put(words[0], 0);
                                    wordmap.put(words[1], 0);
                                    wordmap.put(words[2], 0);

        //                        if (cache.containsKey(key)) {
        //                            exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
        //                                    + cache.get(key) + "\n" + "\n");
        //                            return;
        //                        }

                                    List<String> result = null;
                                    try {
                                        result = HikariDao.retrieveRangeWordCount(start_date, end_date, start_userid, end_userid);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        System.out.println(e);
                                    }
                                    for (String str: result) {
                                        if (!str.contains(words[0]) && !str.contains(words[1]) && !str.contains(words[2])) {
                                            continue;
                                        }
                                        String[] parts = str.split("\\|");
                                        for (int i = 0; i < parts.length; i++) {
                                            String[] wordcount = parts[i].split(":");
                                            String word = wordcount[0];
                                            if (wordmap.containsKey(word)) {
                                                wordmap.put(word, wordmap.get(word) + Integer.parseInt(wordcount[1]));
                                            }
                                        }
                                    }

                                    //                        cache.put(key, result);
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(TEAM_ID+","+TEAM_AWS_ACCOUNT_ID+"\n"
                                            + words[0] + ":" + wordmap.get(words[0]) + "\n"
                                            + words[1] + ":" + wordmap.get(words[1]) + "\n"
                                            + words[2] + ":" + wordmap.get(words[2]) + "\n");
                                    httpServerExchange.getResponseSender().send(stringBuilder.toString());
                                }
                            });
                        } else {
			    			exchange.setDispatchExecutor(cachedThreadPool);
                            if (exchange.isInIoThread()) {
                                exchange.dispatch(this);
                                return;
                            }

                            final Map<String, Deque<String>> params = exchange.getQueryParameters();
                            final int seq = Integer.parseInt(params.get("seq").getFirst());
                            final String tweetid = params.get("tweetid").getFirst();
                            final String[] fields = params.get("fields").getFirst().split(",");
                            final String[] payload = params.get("payload").getFirst().split(",");
                            final String op = params.get("op").getFirst();

                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");

						    synchronized(syncMap) {
						    	if (!syncMap.containsKey(tweetid)) {
						    		syncMap.put(tweetid, new Object());
								}
						    }
                            
			    			synchronized(latestSeqs) {
                                if (!latestSeqs.containsKey(tweetid)) {
                                        latestSeqs.put(tweetid, 0);
                                }
                            }	
                            
						    Object o = null;	
						    synchronized(syncMap) {
								o = syncMap.get(tweetid);	
						    }
                           
			    			synchronized(o) {
                            	while (seq - 1 != latestSeqs.get(tweetid)) {
                                    o.wait();
                           	 	}
			    			}

                            if (op.equals("set")) {
                                //put into the batch
                                exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
                                        + "success" + "\n");	
                                HikariDao.updateRecord(tweetid, fields, payload);
                                synchronized(o) {
							   		latestSeqs.put(tweetid, seq);
							   		o.notifyAll();
								}
                            }

                            if (op.equals("get")) {
                                String result = null;

                               result = HikariDao.getRecord(tweetid, fields[0]);
                               result = result.replaceAll(" ", "+");

                               exchange.getResponseSender().send(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n"
                                        + result + "\n");
								synchronized(o) {
									latestSeqs.put(tweetid, seq);
									o.notifyAll();
								}
                            }
                            exchange.endExchange();
                        }
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
