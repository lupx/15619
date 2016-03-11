import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * Created by PeixinLu on 16/3/10.
 */
public class SecondMapper {
    public static void main(String[] args) {
        try {
            BufferedReader br =
                    new BufferedReader (new InputStreamReader(System.in));
            String tempString = null;

            while ((tempString = br.readLine()) != null) {
                System.out.println(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
