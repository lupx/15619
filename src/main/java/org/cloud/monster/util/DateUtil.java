package org.cloud.monster.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Gets the time
 * @author  Peixin Lu
 */
public class DateUtil {

    public static String currentTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        sdfDate.setTimeZone(TimeZone.getTimeZone("EST"));
        Date now = new Date();
        return sdfDate.format(now);
    }

    public static void main(String[] args) {
        System.out.println(currentTime());
    }
}
