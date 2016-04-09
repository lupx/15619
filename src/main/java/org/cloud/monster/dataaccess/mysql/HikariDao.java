package org.cloud.monster.dataaccess.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PeixinLu on 16/3/29.
 */
public class HikariDao {

    private static final String DB_USER = "root";
    private static final String DB_PWD = "monster";

    private static HikariDataSource ds;

    static {
        HikariConfig sqlConfig = new HikariConfig();
        sqlConfig.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        sqlConfig.addDataSourceProperty("url", "jdbc:mysql://localhost:3306/twitter");
        sqlConfig.addDataSourceProperty("user", DB_USER);
        sqlConfig.addDataSourceProperty("password", DB_PWD);
        sqlConfig.setPoolName("SQLPool");
        //sqlConfig.setMaxLifetime(1800000l);
        sqlConfig.setAutoCommit(true);
        sqlConfig.setMinimumIdle(10);
        sqlConfig.setMaximumPoolSize(100);
        sqlConfig.setConnectionTimeout(10000l);
        ds = new HikariDataSource(sqlConfig);
    }


    /**
     * Retrieves Q2 result for given key
     * @param md5key
     * @return
     * @throws Exception
     */
    public static List<String> retrieveTwitter(String md5key) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try {
            con = ds.getConnection();

            pstmt = con.prepareStatement("SELECT t.content from maintable "
                    + " AS t where t.id=?");
            pstmt.setString(1, md5key);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("content"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.close();
            if (pstmt != null) pstmt.close();
            if (rs != null) rs.close();
        }
        return list;
    }


    /**
     * Retrieves the range for given parameters.
     * @param startDate
     * @param endDate
     * @param startUid
     * @param endUid
     * @return list that contains all the word:count pairs in the range
     * @throws Exception
     */
    public static List<String> retrieveRangeWordCount(String startDate,
                                               String endDate,
                                               String startUid,
                                               String endUid)
            throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try {
            con = ds.getConnection();
            String startKey = startUid + startDate;
            String endKey = endUid + endDate;
            pstmt = con.prepareStatement("SELECT content from newquery where id>=? and id<=? and qtime>=? and qtime<=?");
            pstmt.setString(1, startKey);
            pstmt.setString(2, endKey);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("content"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.close();
            if (pstmt != null) pstmt.close();
            if (rs != null) rs.close();
        }
        return list;
    }

}
