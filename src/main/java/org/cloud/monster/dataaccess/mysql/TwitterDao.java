package org.cloud.monster.dataaccess.mysql;

import org.cloud.monster.pojo.Twitter;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Connects to Mysql database and accesses data stored.
 * Provides several interfaces to retrieve data.
 *
 * @author Peixin Lu
 */
public class TwitterDao {

    /**
     * Connection pool.
     */
    private List<Connection> connectionPool = new ArrayList<Connection>();

    private static final String jdbcDriver;

    /**
     * url to different db server.
     */
    private String url;


    private static String tableName;

    private static final String DB_USER_NAME;
    private static final String DB_PASSWORD;

    static {
        Properties properties = new Properties();
        try {
            properties.load(TwitterDao.class.getResourceAsStream("/info.properties"));
        } catch (IOException io) {
            System.out.println(io);
        }

        jdbcDriver = properties.getProperty("driver");
        tableName = properties.getProperty("table");
        DB_USER_NAME = properties.getProperty("username");
        DB_PASSWORD = properties.getProperty("pwd");
    }

    /**
     * Gives a specific db url to connect.
     * @param url
     */
    public TwitterDao(String url) throws Exception {
        this.url = url;
//        this.initializePool(url);
    }

    private void initializePool(String url)  throws  Exception {
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw e;
        }
        for (int i = 0 ; i < 1000; i++) {
            try {
                connectionPool.add(DriverManager.getConnection(url, DB_USER_NAME, DB_PASSWORD));
            } catch (SQLException e) {
                throw e;
            }
        }
    }


    /**
     * Retrieve Twitter by userId & hashTag
     * @param userId
     * @param hashTag
     * @return
     */
    public List<String> retrieveTwitter(String userId, String hashTag) throws Exception {
        Connection con = null;
        try {
            con = getConnection();

//            PreparedStatement pstmt = con.prepareStatement("SELECT t.score, t.create_time, t.tweet_id, t.text  from " + tableName
//                    + " AS t where t.user_id=? and t.hashtag=?");
            PreparedStatement pstmt = con.prepareStatement("SELECT t.content from " + tableName
                    + " AS t where t.user_id=? and binary t.hashtag=?"); // test
            pstmt.setString(1, userId);
            pstmt.setString(2, hashTag);
            ResultSet rs = pstmt.executeQuery();

            List<String> list = new ArrayList<String>();
            while (rs.next()) {
                list.add(rs.getString("content"));
            }
            pstmt.close();
            releaseConnection(con);
            return list;
        } catch (Exception e) { // general exception caught here.
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e2) { /* ignore */
            }
            throw e;
        }
    }

    public List<String> retrieveTwitter(String md5key) throws Exception {
        Connection con = null;
        try {
            con = getConnection();

            PreparedStatement pstmt = con.prepareStatement("SELECT t.content  from " + tableName
                    + " AS t where t.id=?");
            pstmt.setString(1, md5key);
            ResultSet rs = pstmt.executeQuery();

            List<String> list = new ArrayList<String>();
            while (rs.next()) {
                list.add(rs.getString("content"));
            }
            pstmt.close();
            releaseConnection(con);
            return list;
        } catch (Exception e) { // general exception caught here.
            try {
                if (con != null)
                    con.close();
            } catch (SQLException e2) { /* ignore */
            }
            throw e;
        }
    }

    private Connection getConnection() throws Exception {
//        synchronized (connectionPool) {
            if (connectionPool.size() > 0) {
                return connectionPool.remove(connectionPool.size() - 1);
            }
//        }

        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw e;
        }

        try {
            return DriverManager.getConnection(url, DB_USER_NAME, DB_PASSWORD);
        } catch (SQLException e) {
            throw e;
        }
    }

    private void releaseConnection(Connection con) {
//        synchronized (connectionPool) {
            connectionPool.add(con);
//        }
    }


}
