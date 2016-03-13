package org.cloud.monster.dataaccess.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.cloud.monster.cache.LRUCache;
import org.cloud.monster.pojo.Twitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author  Peixin Lu
 */
public class HBaseDao {

    /**
     * The private IP address of HBase master node.
     */
    private static String zkAddr;

    /**
     * The name of your HBase table.
     */
    private static String tname;


    private static TableName tableName;

    /**
     * Twitter Table handler.
     */
    private static Table twitterTable;


    /**
     * HBase connection pool.
     */
    private static List<Connection> conn_pool;


    /**
     * HBase configuration
     */
    private static Configuration conf;


    /**
     * Logger.
     */
    private final static Logger logger = Logger.getRootLogger();

    static{
        Properties properties = new Properties();
        try {
            properties.load(HBaseDao.class.getResourceAsStream("/info.properties"));
        } catch (IOException io) {
            System.out.println(io);
        }
        tname = properties.getProperty("hbtable");
        zkAddr = properties.getProperty("zookeeper");


        logger.setLevel(Level.ERROR);

        conf = HBaseConfiguration.create();
        conf.set("hbase.master", zkAddr + ":60000");
        conf.set("hbase.zookeeper.quorum", zkAddr);
        conf.set("hbase.zookeeper.property.clientport", "2181");
        if (!zkAddr.matches("\\d+.\\d+.\\d+.\\d+")) {
            System.out.print("HBase not configured!");
        }
        conn_pool = new ArrayList<>();

    }

    private synchronized static Connection getConnection() throws IOException {
        if (conn_pool.size() > 0) {
            return conn_pool.remove(conn_pool.size() - 1);
        }
        try {
            return ConnectionFactory.createConnection(conf);
        } catch (IOException io) {
            throw io;
        }
    }

    private static void releaseConnection(Connection con) {
        conn_pool.add(con);
    }


    /**
     * Searches one row by using given rowKey
     * @param rowKey the rowKey is userid#hashtag
     * @return this row
     * @throws IOException
     */
    public static List<Twitter> retrieveTweets(String rowKey) throws IOException {
        Connection conn = getConnection();
        Table twitterTable = conn.getTable(TableName.valueOf(tname));
        List<Twitter> list = new ArrayList<>();
        Get get = new Get(Bytes.toBytes(rowKey));
        Result rs = twitterTable.get(get);

        if (rs.isEmpty()) return list;

        // Reading values from Result class object
        String twitterRaw = Bytes.toString(rs.getValue(Bytes.toBytes("info"),Bytes.toBytes("tweet_id")));
        String timeRaw = Bytes.toString(rs.getValue(Bytes.toBytes("info"),Bytes.toBytes("create_time")));
        String scoreRaw = Bytes.toString(rs.getValue(Bytes.toBytes("info"),Bytes.toBytes("score")));
        String textRaw = Bytes.toString(rs.getValue(Bytes.toBytes("info"),Bytes.toBytes("text")));



        String[] tids = twitterRaw.split("#\"");
        String[] times = timeRaw.split("#\"");
        String[] scores = scoreRaw.split("#\"");
        String[] texts = textRaw.split("#\"");
        for (int i = 0; i < tids.length; i++) {
            Twitter t = new Twitter();
            t.setTwitterId(tids[i]);
            t.setTime(times[i]);
            t.setScore(scores[i]);
            t.setText(texts[i]);
            list.add(t);
        }
        Collections.sort(list);

        if (twitterTable != null) {
            twitterTable.close();
        }

        releaseConnection(conn);
        return list;
    }


    public static void main(String[] args) throws IOException {
        String rowKey = null;
        if (args == null || args.length == 0) rowKey = "2324314004#LinkedIn";
        else rowKey = args[0] + "#" + args[1];

        List<Twitter> list = null;
        try {
            list = retrieveTweets(rowKey);
        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("Something wrong happened!");
        }

        for (Twitter t : list) {
            System.out.println(t.toString());
        }
    }

}
