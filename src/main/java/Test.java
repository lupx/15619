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
        String b = "naïve \uD83D\uDC4C (´×ω×`)";
        String c = "https://pbs.twimg.com/media/BnqgpJyIYAAW8jm.jpg";
        System.out.println(c);
    }
}
