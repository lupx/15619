//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
///**
// * This Mapper corporates with SecondReducer to remove
// * duplicated records based on result that generated from first MR.
// *
// * This Mapper gets System.in and do nothing.
// *
// */
//public class SecondMapper {
//    public static void main(String[] args) {
//        try {
//            BufferedReader br =
//                    new BufferedReader (new InputStreamReader(System.in));
//            String tempString = null;
//
//            while ((tempString = br.readLine()) != null) {
//                System.out.println(tempString);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
