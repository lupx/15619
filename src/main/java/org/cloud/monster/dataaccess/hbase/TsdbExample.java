package org.cloud.monster.dataaccess.hbase;

import org.hbase.async.Config;
import org.hbase.async.GetRequest;
import org.hbase.async.HBaseClient;
import org.hbase.async.KeyValue;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * Created by PeixinLu on 16/3/28.
 */
public class TsdbExample {
    private static HBaseClient client = null;
    private static Config config = null;

//    private static final byte[] TABLE = new byte[] {'t', 'w', 'i', 't', 't','e', 'r'};
//    private static final byte[] ROW_KEY = new byte[] {'t', 'w', 'i', 't', 't','e', 'r'};

    public static void main(String[] args) {
        try {
            config = new Config("/tsdb.conf");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred in config loading ... ");
        }

        Executor executor = Executors.newCachedThreadPool();
        client = new HBaseClient(config, executor);

        String rowKey = "c72d1761dd85c9fde78282c5a9189ba1";

        GetRequest get = new GetRequest("twitter", rowKey);
        get.family("info").qualifier("data");

        try {
            final ArrayList<KeyValue> result = client.get(get).joinUninterruptibly();
            for (KeyValue kv : result) {
                System.out.println("The cell value is :" + new String(kv.value(), "UTF8"));
            }
        } catch (Exception e) {
        } finally {
        }
    }
}
