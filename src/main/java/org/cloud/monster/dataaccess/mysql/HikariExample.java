package org.cloud.monster.dataaccess.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

/**
 * An example class for Hikari.
 * Created by PeixinLu on 16/3/28.
 */
public class HikariExample {
    public static void main (String[] args) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("");
        HikariDataSource ds = new HikariDataSource(config);

        try {

            Connection conn = ds.getConnection();
        } catch (Exception e) {

        }


    }

}
