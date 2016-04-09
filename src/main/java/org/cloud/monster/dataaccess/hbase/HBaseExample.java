package org.cloud.monster.dataaccess.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Created by PeixinLu on 16/3/28.
 */
public class HBaseExample {
    public static void main(String[] args) throws IOException {
        // You need a configuration object to tell the client where to connect.
        // When you create a HBaseConfiguration, it reads in whatever you've set
        // into your hbase-site.xml and in hbase-default.xml, as long as these can
        // be found on the CLASSPATH
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.master", "172.31.10.236" + ":60000");
        config.set("hbase.zookeeper.quorum", "172.31.10.236");
        config.set("hbase.zookeeper.property.clientport", "2181");

        // Next you need a Connection to the cluster. Create one. When done with it,
        // close it. A try/finally is a good way to ensure it gets closed or use
        // the jdk7 idiom, try-with-resources: see
        // https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        //
        // Connections are heavyweight.  Create one once and keep it around. From a Connection
        // you get a Table instance to access Tables, an Admin instance to administer the cluster,
        // and RegionLocator to find where regions are out on the cluster. As opposed to Connections,
        // Table, Admin and RegionLocator instances are lightweight; create as you need them and then
        // close when done.
        //

        Connection connection = ConnectionFactory.createConnection(config);
        try {

            // The below instantiates a Table object that connects you to the "myLittleHBaseTable" table
            // (TableName.valueOf turns String into a TableName instance).
            // When done with it, close it (Should start a try/finally after this creation so it gets
            // closed for sure the jdk7 idiom, try-with-resources: see
            // https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
            Table table = connection.getTable(TableName.valueOf("twitter"));
            try {

                // Now, to retrieve the data we just wrote. The values that come back are
                // Result instances. Generally, a Result is an object that will package up
                // the hbase return into the form you find most palatable.
                Get g = new Get(Bytes.toBytes("c72d1761dd85c9fde78282c5a9189ba1"));
                Result r = table.get(g);
                byte [] value = r.getValue(Bytes.toBytes("info"),
                        Bytes.toBytes("data"));

                // If we convert the value bytes, we should get back 'Some Value', the
                // value we inserted at this location.
                String valueStr = Bytes.toString(value);
                System.out.println("GET: " + valueStr);

                // Sometimes, you won't know the row you're looking for. In this case, you
                // use a Scanner. This will give you cursor-like interface to the contents
                // of the table.  To set up a Scanner, do like you did above making a Put
                // and a Get, create a Scan.  Adorn it with column names, etc.
                // Close your table and cluster connection.
            } finally {
                if (table != null) table.close();
            }
        } finally {
            connection.close();
        }
    }
}
