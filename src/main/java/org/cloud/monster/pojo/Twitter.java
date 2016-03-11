package org.cloud.monster.pojo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maps to response entity.
 * @author Peixin Lu
 */
public class Twitter implements Comparable<Twitter> {

    private String score;

    private String twitterId;

//    private String userId;

    private String time;

    private String text;

//    private String hashTag;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    public String getHashTag() {
//        return hashTag;
//    }

//    public void setHashTag(String hashTag) {
//        this.hashTag = hashTag;
//    }


    @Override
    public String toString() {
        return score + ":" + time + ":" + twitterId + ":" + text;
    }

    /**
     * Compares two Twitters according to rules:
     * (1) Bigger score ranks higher;
     * (2) When same score, earlier time ranks higher;
     * (3) When same time, smaller twitterId ranks higher;
     * @param o
     * @return
     */
    @Override
    public int compareTo(Twitter o) {
        if (!this.score.equals(o.score)) {
            return new BigDecimal(o.score).compareTo(new BigDecimal(this.score));
        }
        if (!this.time.equals(o.time)) {
            return this.time.compareTo(o.time);
        }
        if (!this.twitterId.equals(o.twitterId)) {
            return this.twitterId.compareTo(o.twitterId);
        }
        return 0;
    }

    public static void main(String[] args) {
        Twitter t1 = new Twitter();
        Twitter t2 = new Twitter();
        Twitter t3 = new Twitter();
        Twitter t4 = new Twitter();

        t1.setScore("0.308");
        t1.setTime("2014-04-15 11-42-18");
        t1.setTwitterId("456034778891169793");
        t1.setText("adfadfadsffadsfdsfasdf");

        t2.setScore("0.267");
        t2.setTime("2014-06-01 19-34-25");
        t2.setTwitterId("473185820636356608");
        t2.setText("adfadfadsffadsfdsfasdf");

        t3.setScore("0.267");
        t3.setTime("2014-04-15 11-42-18");
        t3.setTwitterId("456034778891169793");
        t3.setText("adfadfadsffadsfdsfasdf");

        t4.setScore("0.267");
        t4.setTime("2014-04-15 11-42-18");
        t4.setTwitterId("476034778891169793");
        t4.setText("adfadfadsffadsfdsfasdf");

        List<Twitter> list = new ArrayList<>();

//        list.add(t1);
//        list.add(t2);
//        list.add(t3);
//        list.add(t4);

        Collections.sort(list);

        for (Twitter t : list) {
            System.out.println(t.toString() + "\n");
        }
    }
}
