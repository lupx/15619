//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
///**
// * This Mapper corporates with ThirdReducer to sort records and to remove
// * duplicated records based on result that generated from first MR.
// *
// */
//public class ThirdMapper {
//    public static void main(String[] args) {
//        try {
//            BufferedReader br =
//                    new BufferedReader (new InputStreamReader(System.in));
//            String tempString = null;
//
//            while ((tempString = br.readLine()) != null) {
//                String[] strs = tempString.split("\\t");
//
//                String key = strs[1] + "#\"" + strs[5];
//                String value = strs[0] + "#\"" + strs[2] + "#\"" + strs[3] + "#\"" + strs[4];
//
//                System.out.println(key + "\t" + value);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
