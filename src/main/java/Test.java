import java.sql.Timestamp;
import java.util.TimeZone;

/**
 * Some test issues.
 * @author Peixin Lu
 */
public class Test {
    public static void main(String[] args) {
//        String timestamp1 = new Timestamp(System.currentTimeMillis()
//                + TimeZone.getTimeZone("EST").getRawOffset()).toString();
//        String timestamp2 = new Timestamp(System.currentTimeMillis()
//                + TimeZone.getTimeZone("EST").getRawOffset()).toString();
//
//        System.out.println(timestamp1.compareTo(timestamp2));
//        String a = "312lf;ahfkd";
//        a = a.toUpperCase();
//        System.out.println(a);
        char a = '2';
//        System.out.println(hash("343123fadf"));

        System.out.println("YNEQREREBJUHTTVATOHATANRYHEBERNBNOYRZCAEOVGYHZNUGYYGBCSCXBPRVNTAHEROBFNYNPNJNVHFZHPFBQNQYCZHEGARPGFB".length());
    }

    private static int hash(String key) {
        if (key.equals("a")) return 0;
        if (key.equals("b")) return 1;
        if (key.equals("c")) return 2;

        if (key.length() == 5) return 0;
        if (key.length() == 6) return 1;
        if (key.length() == 9) return 2;

        key = key.toUpperCase();
        char firstC = key.charAt(0);
        if (firstC >= '0' && firstC <= '9') return (firstC - '0') % 3;

        if (firstC >= 'A' && firstC <= 'I') return 0;
        if (firstC >= 'J' && firstC <= 'R') return 1;
        if (firstC >= 'S' && firstC <= 'Z') return 2;
        return 0;
    }
}
