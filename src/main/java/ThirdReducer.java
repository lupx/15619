//import org.cloud.monster.util.MD5Util;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * This Reducer corporates with SecondMapper to sort records and to remove
// * duplicated records based on result that generated from first MR.
// *
// * Also, this reducer md5s the combined key : userid+hashtag.
// */
//public class ThirdReducer {
//
//    public static void main(String[] args) {
//        try {
//            BufferedReader br =
//                    new BufferedReader(new InputStreamReader(System.in));
//            //Initialize Variables
//            String input;
//            String currentKey = null;
//            List< TwitterObject> list = new ArrayList<>();
//
//            String userId = null;
//            String hashTag = null;
//
//            String twitterId = null;
//            String score = null;
//            String time = null;
//            String text = null;
//
//            // to filter out duplicated twitter.
//            String currentTwitterId = null;
//
//            while ((input = br.readLine()) != null) {
//
//                String key = input.split("\\t")[0];
//                String value = input.split("\\t")[1];
//
//                hashTag = key.split("#\"")[1];
//                userId = key.split("#\"")[0];
//
//                twitterId = value.split("#\"")[0];
//                score = value.split("#\"")[2];
//                time = value.split("#\"")[1];
//                text = value.split("#\"")[3];
//
//                TwitterObject t = new TwitterObject();
//                t.setHashTag(hashTag);
//                t.setUserId(userId);
//
//                t.setText(text);
//                t.setScore(score);
//                t.setTime(time);
//                t.setTwitterId(twitterId);
//
//                if (currentKey == null) {
//                    currentKey = key;
//                    list.add(t);
//                } else {
//                    if (!key.equals(currentKey)) {
//                        Collections.sort(list);
//                        removeDup(list);//remove duplicated.
//
//                        StringBuilder sb = new StringBuilder();
//                        String md5key = MD5Util
//                                .getMD5(list.get(0).getUserId() + list.get(0).getHashTag()); // md5ed idkey
//
//                        for (TwitterObject tt : list) {
//                           sb.append(tt.toString()).append("\\n");
//                        }
//
//                        System.out.println(md5key + "\t" + sb.toString());
//                        list.clear();
//                        list.add(t);
//                        currentKey = key;
//                    } else {
//                        list.add(t);
//                    }
//                }
//            }
//
//            if (list.size() > 0) {
//                Collections.sort(list);
//                removeDup(list);//remove duplicated.
//
//                StringBuilder sb = new StringBuilder();
//                String md5key = MD5Util
//                        .getMD5(list.get(0).getUserId() + list.get(0).getHashTag()); // md5ed idkey
//
//                for (TwitterObject tt : list) {
//                    sb.append(tt.toString()).append("\\n");
//                }
//
//                System.out.println(md5key + "\t" + sb.toString());
//            }
//
//        } catch(IOException io) {
//            io.printStackTrace();
//        }
//    }
//
//    /**
//     * Removes duplicated records in the given list.
//     * @param list
//     * @return new list
//     */
//    private static List<TwitterObject> removeDup(List<TwitterObject> list) {
//        List<TwitterObject> newlist = new ArrayList<TwitterObject>();
//        TwitterObject currentTO = null;
//        for (TwitterObject to : list) {
//            if (currentTO == null) currentTO = to;
//            else {
//                if (!currentTO.toString().equals(to.toString())) {
//                    newlist.add(to);
//                }
//            }
//        }
//        if (currentTO != null) newlist.add(currentTO);
//        return newlist;
//    }
//
//}
//
///**
// * Used to sort records
// * by implementing Comparable.
// */
//class TwitterObject implements Comparable<TwitterObject> {
//
//    private String score;
//
//    private String twitterId;
//
//    private String userId;
//
//    private String time;
//
//    private String text;
//
//    private String hashTag;
//
//    public String getScore() {
//        return score;
//    }
//
//    public void setScore(String score) {
//        this.score = score;
//    }
//
//    public String getTwitterId() {
//        return twitterId;
//    }
//
//    public void setTwitterId(String twitterId) {
//        this.twitterId = twitterId;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public String getHashTag() {
//        return hashTag;
//    }
//
//    public void setHashTag(String hashTag) {
//        this.hashTag = hashTag;
//    }
//
//
//    @Override
//    public String toString() {
//        return score + ":" + time + ":" + twitterId + ":" + text;
//    }
//
//    /**
//     * Compares two Twitters according to rules:
//     * (1) Bigger score ranks higher;
//     * (2) When same score, earlier time ranks higher;
//     * (3) When same time, smaller twitterId ranks higher;
//     *
//     * @param o
//     * @return
//     */
//    @Override
//    public int compareTo(TwitterObject o) {
//        if (!this.score.equals(o.score)) {
//            return new BigDecimal(o.score).compareTo(new BigDecimal(this.score));
//        }
//        if (!this.time.equals(o.time)) {
//            return this.time.compareTo(o.time);
//        }
//        if (!this.twitterId.equals(o.twitterId)) {
//            return this.twitterId.compareTo(o.twitterId);
//        }
//        return 0;
//    }
//}
