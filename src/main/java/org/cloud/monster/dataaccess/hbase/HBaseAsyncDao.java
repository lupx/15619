package org.cloud.monster.dataaccess.hbase;

import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.hbase.async.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * Created by PeixinLu on 16/3/28.
 */
public class HBaseAsyncDao {
    private static HBaseClient client = null;
    private static Config config = null;

    static {
        try {
            config = new Config("/home/ubuntu/undertow/target/classes/tsdb.conf");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred in config loading ... ");
        }

        Executor executor = Executors.newCachedThreadPool();
        client = new HBaseClient(config, executor);
    }

    /**
     * Searches one row by using given rowKey
     * @param rowKey the rowKey is userid#hashtag
     * @return this row
     * @throws IOException
     */
    public static String retrieveTweets(String rowKey) throws IOException {

        GetRequest get = new GetRequest("twitter", rowKey);
        get.family("info").qualifier("data");

        String str = null;
        try {
            final ArrayList<KeyValue> result = client.get(get).joinUninterruptibly();
            KeyValue kv = result.get(0);
            str = new String(kv.value(), "UTF8");
        } catch (Exception e) {
        } finally {
        }
        if (str == null) return null;
        return process(str);
    }

    private static String process(String str) {
        return str.replace("\\n", "\n").replace("\\r", "\r")
                .replace("\\t", "\t").replace("\\\\", "\\").replace("\\\"", "\"").replace("\\\'", "\'");
    }


    public static List<String> retrieveRange(String startKey, String endKey, String startDate, String endDate) {
        List<String> rst = new ArrayList<String>();
        Scanner scanner = client.newScanner("newtwitter");
        try {
            scanner.setFamily("info");
            scanner.setQualifier("c");

            /** adds startRow and endRow **/
            scanner.setStartKey(startKey);
            scanner.setStopKey(endKey);

            scanner.setServerBlockCache(true);

            /** adds filters **/
//            FilterComparator comp1 = new BinaryComparator(Bytes.toBytes(Integer.parseInt(startDate)));
//            ScanFilter startFilter = new ValueFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, comp1) ; // >= startDate
//            FilterComparator comp2 = new BinaryComparator(Bytes.toBytes(Integer.parseInt(endDate)));
//            ScanFilter endFilter = new ValueFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, comp2) ; // <= endDate
//
//            List<ScanFilter> filters = new ArrayList<ScanFilter>();
//
//            filters.add(startFilter);
//            filters.add(endFilter);

            //scanner.setFilter(new FilterList(filters, FilterList.Operator.MUST_PASS_ALL));

            ArrayList<ArrayList<KeyValue>> listResult = scanner.nextRows().joinUninterruptibly();
            for (ArrayList<KeyValue> list : listResult) {
                String[] values = new String(list.get(0).value(), "UTF8").split("#");
                String date = values[0];
                if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
                    rst.add(values[1]);
                }
            }

        } catch (Exception e) {

        } finally {
            scanner.close();
        }
        return rst;
    }

}
