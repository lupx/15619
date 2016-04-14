package org.cloud.monster.util;

import java.security.MessageDigest;

/**
 * MD5 util class
 * @author Peixin Lu
 */
public class MD5Util {
    public static void main(String[] args) {
        String result = getMD5("1646547d55TheLostPlanetinSeoul");
        System.out.println(result);
    }

    /**
     * generate md5 String
     * @param message
     * @return
     */
    public static String getMD5(String message) {
        String md5str = "";
        try {
            //1. create an object that provides digest function
            MessageDigest md = MessageDigest.getInstance("MD5");

            //2 message to byte array
            byte[] input = message.getBytes("UTF8");

            //3 calculate digest from byte array
            byte[] buff = md.digest(input);

            //4 bytes to Hex string
            md5str = bytesToHex(buff);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * byte to hex
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if(digital < 0) {
                digital += 256;
            }
            if(digital < 16){
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }
}
