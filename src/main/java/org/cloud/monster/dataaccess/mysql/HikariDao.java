package org.cloud.monster.dataaccess.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.cloud.monster.pojo.Record;

import java.sql.*;
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

    /**
     * Update the record for query4
     * @param tweetid
     * @param fieldsName
     * @param fields
     * @throws Exception
     */
    public static void updateRecord(String tweetid, String[] fieldsName,
                                    String[] fields) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = ds.getConnection();

            List<String> newFieldsName = new ArrayList<>();
            List<String> newFields = new ArrayList<>();

            int len = Math.min(fields.length, fieldsName.length);

            for (int i = 0; i < len; i++) {
                if (fields[i].length() > 0) {
                    newFieldsName.add(fieldsName[i]);
                    newFields.add(fields[i]);
                }
            }

            StringBuilder sqlInsertPartA = new StringBuilder();
            sqlInsertPartA.append("INSERT INTO q4table (tweetid,");

            StringBuilder sqlInsertPartB = new StringBuilder();
            sqlInsertPartB.append("VALUES (?,");

            StringBuilder sqlUpdate = new StringBuilder();
            sqlUpdate.append("ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < newFieldsName.size(); i++) {
                sqlInsertPartA.append(newFieldsName.get(i) + ",");
                sqlInsertPartB.append("?,");
                sqlUpdate.append(newFieldsName.get(i) + "=?,");
            }

            sqlInsertPartA.deleteCharAt(sqlInsertPartA.length() - 1);
            sqlInsertPartA.append(") ");

            sqlInsertPartB.deleteCharAt(sqlInsertPartB.length() - 1);
            sqlInsertPartB.append(") ");

            sqlUpdate.deleteCharAt(sqlUpdate.length() - 1);

            String sql = new StringBuilder().append(sqlInsertPartA).append(sqlInsertPartB)
                    .append(sqlUpdate).toString();

            ps = con.prepareStatement(sql);

            ps.setString(1, tweetid);

            for (int i = 0; i < newFields.size(); i++) {
                ps.setString(i + 2, newFields.get(i));
                ps.setString(i + newFields.size() + 2, newFields.get(i));
            }

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
    }


    public static String generateSql(String tweetid, List<String> fieldNames, List<String> fieldValues) {
        StringBuilder sqlInsertPartA = new StringBuilder();
        sqlInsertPartA.append("INSERT INTO q4table (tweetid,");

        StringBuilder sqlInsertPartB = new StringBuilder();
        sqlInsertPartB.append("VALUES (" + tweetid + ",");

        StringBuilder sqlUpdate = new StringBuilder();
        sqlUpdate.append("ON DUPLICATE KEY UPDATE ");
        for (int i = 0; i < fieldNames.size(); i++) {
            sqlInsertPartA.append(fieldNames.get(i) + ",");
            sqlInsertPartB.append(fieldValues.get(i) + ",");
            sqlUpdate.append(fieldNames.get(i) + "=" + fieldValues.get(i) + ",");
        }

        sqlInsertPartA.deleteCharAt(sqlInsertPartA.length() - 1);
        sqlInsertPartA.append(") ");

        sqlInsertPartB.deleteCharAt(sqlInsertPartB.length() - 1);
        sqlInsertPartB.append(") ");

        sqlUpdate.deleteCharAt(sqlUpdate.length() - 1);

        String sql = new StringBuilder().append(sqlInsertPartA).append(sqlInsertPartB)
                .append(sqlUpdate).toString();
        return sql;
    }
    public static void batchUpdate(Map<String, Record> map) throws Exception {
        Connection con = null;
        Statement ps = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            ps = con.createStatement();
            for (Map.Entry<String, Record> entry : map.entrySet()) {
                String s = generateSql(entry.getKey(), entry.getValue().getFieldsNames(),
                        entry.getValue().getFields());
                ps.addBatch(s);
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
    }



    public static String getRecord(String tweetid, String field) throws Exception {
        StringBuilder res = new StringBuilder();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            ps = con.prepareStatement("SELECT " + field + " FROM q4table WHERE tweetid=?");
            ps.setString(1, tweetid);

            rs = ps.executeQuery();

            while (rs.next()) {
                String str = rs.getString(field);
                if (str != null) res.append(rs.getString(field));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
        return res.toString();
    }

}
