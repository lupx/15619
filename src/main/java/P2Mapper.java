//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
///**
// * This Mapper corporates with P2Reducer,
// * calculates words count based on one userid + timestamp.
// *
// * @author Peixin Lu
// */
//public class P2Mapper {
//    public static void main(String[] args) {
//        try {
//            BufferedReader br =
//                    new BufferedReader (new InputStreamReader(System.in));
//            String tempString = null;
//
//            while ((tempString = br.readLine()) != null) {
//                String[] strs = tempString.split("\\t");
//
//                String twitterId = strs[0]; // eliminates this.
//                String uid = strs[1];
//                String time = strs[2];
//
//                StringBuilder value = new StringBuilder();
//                // from index=3 to strs' last element
//                for (int i = 3; i < strs.length; i++) {
//                    value = value.append(strs[i]).append("|");
//                }
//                value.deleteCharAt(value.length() - 1);//delete the last '|'
//
//                /** "time#uid" is the key,  "wordcounts" is the value */
//                System.out.println(time + "#" + uid + "\t" + value.toString());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
