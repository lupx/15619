package org.cloud.monster.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;

/**
 *
 * @author Zhonghe Yang
 */
public class Decrypt {
    static private final String secretString;
    static private final BigInteger secretKey;
    static private final BigInteger ZERO;
    static {
        Properties properties = new Properties();
        try {
            properties.load(Decrypt.class.getResourceAsStream("/info.properties"));
        } catch (IOException io) {
        }
        secretString = properties.getProperty("secret");
        secretKey = new BigInteger(secretString);
        ZERO = new BigInteger("0");
    }

    public static void main(String[] args) {
//        System.out.println(decrypt("4024123659485622445001958636275419709073611535463684596712464059093821", "URYEXYBJB"));

//        BigInteger b = new BigInteger("29824881671662318316918906016914781134706550990334208872849272858917042444015119314823149633067286528");
//
//        System.out.println(getGCD(b));
//
        BigInteger b1 = new BigInteger("29824881671662318316918906016914781134706550990334208872849272858917042444015119314823149633067286528");
        BigInteger b2 = new BigInteger("64266330917908644872330635228106713310880186591609208114244758680898150367880703152525200743234420230");
//        BigInteger divisor = new BigInteger("18762");
        System.out.println(b2.gcd(b1));
//        long start = System.currentTimeMillis();
//        BigInteger b3 = getGCD(b1);
//        long end = System.currentTimeMillis();
//        System.out.println(b3);
//        System.out.println("cost :" + (end - start) + "ms");

//        System.out.println(2100 / 140);
//        System.out.println(gcd(100,500));
    }

    /**
     * ^^^^^ Need to judge if the input is valid ??? ^^^^
     * ^^^^^ What if key or message is not valid ^^^^
     * ^^^^^ Invalid Message: null || length == 0 || not 4,9,25...
     * @param keyParameter key parameter from url
     * @param message message from url
     * @return decrypted message
     */
    public static String decrypt(String keyParameter, String message) {
        if (message == null) return null;
        int keyZ = getKeyZ(keyParameter);
//        System.out.println("keyZ = " + keyZ);
        char[] charArray = getCharArray(message, keyZ);
        return getDecryptedMessage(charArray);
    }

    // step1 : Input calculate key Z : GCD
    /**
     * Generates the key Z from the parameter : key
     * @param keyParameter
     * @return keyZ
     */
    static int getKeyZ (String keyParameter) {
        BigInteger keyY = new BigInteger(keyParameter);
        BigInteger gcd = secretKey.gcd(keyY);
        return 1 + gcd.mod(new BigInteger("25")).intValue();
    }

    /**
     *
     * @param messageParameter
     * @param keyZ
     * @return charArray of the decrypted char values but not correct order
     */
    // step2 : Caesarify get intermediate message with key Z
    static char[] getCharArray(String messageParameter, int keyZ) {
        char[] charArray = messageParameter.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] - 'A' >= keyZ) {
                charArray[i] = (char)(charArray[i] - keyZ);
//                System.out.println("new char " + charArray[i]);
            } else {
                int gap = charArray[i] - 'A';
//                System.out.println("gap = " + gap);
                charArray[i] = (char)('Z' - (keyZ-gap) + 1);
            }
        }
        return charArray;
    }

    /**
     *
     * @param charArray with decrypted char values but not correct order
     * @return String decrypted message
     */
    // step3 : Spiralize get decrypted message from the matrix
    static String getDecryptedMessage(char[] charArray) {
        StringBuilder sb = new StringBuilder();
        int len = (int) Math.sqrt(charArray.length);
//        System.out.println("matrix length = " + len);
        // initialize matrix with values from charArray
        char[][] matrix = new char[len][len];
        for(int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                matrix[i][j] = charArray[i * len + j];
            }
        }
        int currentLen = len;
        while(currentLen > 0) {
//            System.out.println(currentLen);
            int start = (len - currentLen) / 2;
//            System.out.println("start = " + start);
            int end = start + currentLen - 1;
//            System.out.println("end = " + end);
            for (int i = 0; i < currentLen; i++) {
                sb.append(matrix[start][start + i]);
            }
            for (int i = 1; i < currentLen; i++) {
                sb.append(matrix[start + i][end]);
            }
            for (int i = 1; i < currentLen; i++) {
                sb.append(matrix[end][end - i]);
            }
            for (int i = 1; i < currentLen - 1; i++) {
                sb.append(matrix[end - i][start]);
            }
            currentLen = currentLen - 2;
        }
        return sb.toString();

    }

    static BigInteger getGCD(BigInteger num2) {
        BigInteger num1 = new BigInteger(secretString);
        while(num2.compareTo(ZERO) != 0) {
            BigInteger tem = num2;
            num2 = num1.mod(num2);
            num1 = tem;
        }
        return num1;
    }

    static BigInteger getGCD(BigInteger num1, BigInteger num2) {

        while (num1.compareTo(num2) != 0) {
            if (num1.compareTo(num2) > 0) {
                num1 = num1.subtract(num2);
            }
            if (num1.compareTo(num2) < 0) {
                num2 = num2.subtract(num1);
            }
        }
        return num1;
    }



    int gys1(int m, int n) {
        int k,y;
        if(m<n) {
            k=m;
            m=n;
            n=k;
        }
        while(m % n!=0) {
            y=m%n;
            m=n;
            n=y;
        }
        return n;
    }

    static int gcd(int m, int n) {
        while(m!=n) {
            if(m>n)
                m=m-n;
            if(m<n)
                n=n-m;
        }
        return m;
    }
}
