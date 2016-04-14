//package org.cloud.monster.dataaccess.hbase;
//
////import org.apache.hadoop.conf.Configuration;
////import org.apache.hadoop.hbase.HBaseConfiguration;
////import org.apache.hadoop.hbase.TableName;
////import org.apache.hadoop.hbase.client.*;
////import org.apache.hadoop.hbase.util.Bytes;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.TableName;
//import org.apache.hadoop.hbase.client.*;
////import org.apache.hadoop.hbase.ipc.HBaseClient;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.apache.hadoop.ipc.Server;
//import org.apache.log4j.Logger;
//import org.cloud.monster.util.MD5Util;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Properties;
//
///**
// *
// * @author  Peixin Lu
// */
//public class HBaseNewDao {
//
//    /**
//     * The private IP address of HBase master node.
//     */
//    private static String zkAddr;
//
//    /**
//     * The name of your HBase table.
//     */
//    private static String tname;
//
//
////    private static TableName tableName;
//
//    /**
//     * Twitter Table handler.
//     */
////    private static Table twitterTable;
//
//
//    private static HTableInterface twitterTable;
//
//    /**
//     * HBase connection pool.
//     */
////    private static List<Connection> conn_pool;
//
//
////    private static List<HConnection> conn_pool;
//
////    /**
////     * HBase configuration
////     */
//    private static Configuration conf;
//
//    private static Connection conn;
//
//    /**
//     * Logger.
//     */
//    private final static Logger logger = Logger.getRootLogger();
//
//    static{
//        Properties properties = new Properties();
//        try {
//            properties.load(HBaseNewDao.class.getResourceAsStream("/info.properties"));
//        } catch (IOException io) {
//            System.out.println(io);
//        }
//        zkAddr = properties.getProperty("zookeeper");
//
//        //logger.setLevel(Level.ERROR);
//
//        conf = HBaseConfiguration.create();
//        conf.set("hbase.master", zkAddr + ":60000");
//        conf.set("hbase.zookeeper.quorum", zkAddr);
//        conf.set("hbase.zookeeper.property.clientport", "2181");
//        if (!zkAddr.matches("\\d+.\\d+.\\d+.\\d+")) {
//            System.out.print("HBase not configured!");
//        }
////        conn_pool = new ArrayList<>();
//        try {
//            conn = ConnectionFactory.createConnection(conf);
//        } catch (IOException io) {
//        }
//
//    }
//
////    private static void initializeConnection() throws IOException {
////        zkAddr = "172.31.23.9";
////        logger.setLevel(Level.ERROR);
////        conf = HBaseConfiguration.create();
////        conf.set("hbase.master", zkAddr + ":60000");
////        conf.set("hbase.zookeeper.quorum", zkAddr);
////        conf.set("hbase.zookeeper.property.clientport", "2181");
////        if (!zkAddr.matches("\\d+.\\d+.\\d+.\\d+")) {
////            System.out.print("HBase not configured!");
////            return;
////        }
////        conn = HConnectionManager.createConnection(conf);
////        twitterTable = conn.getTable("twitter");
////    }
//
////    private synchronized static HConnection getConnection() throws IOException {
////        if (conn_pool.size() > 0) {
////            return conn_pool.remove(conn_pool.size() - 1);
////        }
////        try {
////            return HConnectionManager.createConnection(conf);
////        } catch (IOException io) {
////            throw io;
////        }
////    }
////
////    private static void releaseConnection(HConnection con) {
////        conn_pool.add(con);
////    }
//
//
//    /**
//     * Searches one row by using given rowKey
//     * @param rowKey the rowKey is userid#hashtag
//     * @return this row
//     * @throws IOException
//     */
//    public static String retrieveTweets(String rowKey) throws IOException {
//
////        HTableInterface twitterTable = conn.getTable(Bytes.toBytes("twitter"));
//
//        Table twitterTable = conn.getTable(TableName.valueOf("twitter"));
//
//        Get get = new Get(Bytes.toBytes(rowKey.toString()));
//
//        get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("data"));
//
//        Result rs = twitterTable.get(get);
//
//        System.out.println("Get the result ... ");
//
//        if (rs.isEmpty()) return null;
//
//        // Reading values from Result class object
////        String data = Bytes.toString(rs.getValue(Bytes.toBytes("info"),Bytes.toBytes("data")));
//        byte[] bytes = rs.getValue(Bytes.toBytes("info"), Bytes.toBytes("data"));
//        String str = new String(bytes, "UTF8");
//
//        if (twitterTable != null) {
//            twitterTable.close();
//        }
////        conn.close();
////        releaseConnection(conn);
//        return process(str);
//    }
//
//    private static String process(String str) {
//        return str.replace("\\n", "\n").replace("\\r", "\r")
//                .replace("\\t", "\t").replace("\\\\", "\\").replace("\\\"", "\"").replace("\\\'", "\'");
//    }
//
//
//    public static void main(String[] args) throws IOException {
//        String rowKey = null;
//        if (args == null || args.length == 0) rowKey = "2324314004LinkedIn";
//        else rowKey = args[0] + args[1];
//
//        System.out.println("Initialization complete ... ");
//        String result = null;
//        try {
//            result = retrieveTweets(MD5Util.getMD5(rowKey));
//        } catch (IOException io) {
//            io.printStackTrace();
//            System.out.println("Something wrong happened!");
//        }
//        System.out.println(result);
//    }
//
//}
