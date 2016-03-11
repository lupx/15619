import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * Created by PeixinLu on 16/3/10.
 */
public class SecondReducer {

    public static void main(String[] args) {
        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(System.in));
            //Initialize Variables
            String input;
            String current = null;

            while ((input = br.readLine()) != null) {
                /**
                 *  sets the first record as current.
                 *  this sentence will only runs one time.
                 */
                if (current == null) current = input;
                else {
                    /**
                     * If input equals to last record, continue;
                     * otherwise, output current, and make input the current.
                     */
                    if (!input.equals(current)) {
                        System.out.println(current);
                        current = input;
                    }
                }
            }

            if (current != null) {
                // output current string, which is the last record
                System.out.println(current);
            }

        } catch(IOException io) {
            io.printStackTrace();
        }
    }
}
