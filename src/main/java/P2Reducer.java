//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.*;
//
///**
// * This Reducer corporates with P2Mapper to generate
// * records that has unique time+uid as its key, and all the words and their counts as the value.
// * This means, for any one record, there will be a lot of words and their counts in one column.
// *
// * @author Peixin Lu
// */
//public class P2Reducer {
//
//    public static void main(String[] args) {
//        try {
//            BufferedReader br =
//                    new BufferedReader(new InputStreamReader(System.in));
//
//            //Initialize Variables
//            String input;
//            String currentKey = null;
//
//            String time = null;
//            String value = null;
//            String uid = null;
//            String wcvalue = null;
//
//
//            /**
//             * This result stores all records with unique time-uid
//             * In the very end, sort this list, and system.out
//             */
//            List<WordCountObject> result = new ArrayList<WordCountObject>();
//
//
//            /**
//             * This list stores tmp records those have same time and uid.
//             *
//             */
//            List<WordCountObject> tmpList = new ArrayList<WordCountObject>();
//
//
//            while ((input = br.readLine()) != null) {
//
//                String[] strs = input.split("\\t");
//                time = strs[0].split("#")[0];
//                uid = strs[0].split("#")[1];
//
//                value = strs[1];
//
//                /**
//                 *  sets the first record as currentTime.
//                 *  this sentence will only runs one time.
//                 */
//                if (currentKey == null) {
//                    currentKey = strs[0];
//                    WordCountObject wco = new WordCountObject(uid, time, value);
//                    tmpList.add(wco);
//                } else {
//                    /**
//                     * If strs[0] equals to last record, continue;
//                     * otherwise, output current, and make strs[0] the current.
//                     */
//                    if (!strs[0].equals(currentKey)) {
//                        // ends of current key.
//                        // calculate all the words' count in the list
//                        // put all words account into one record and put it into the result list.
//
//                        /** counts all words' count in the tmpList **/
//                        Map<String, Integer> countMap = new HashMap<String, Integer>(); // counts the word
//                        for (WordCountObject wco : tmpList) {
//                            for (String str : wco.getWordcount().split("\\|")) { // for every word-count pair
//                                String word = str.split(":")[0]; // the word
//                                Integer c = Integer.parseInt(str.split(":")[1]); // the count
//                                if (countMap.containsKey(word)) { // for every word
//                                    Integer count = countMap.get(word);
//                                    count += c; // gets the count number
//                                    countMap.put(word, count);
//                                } else {
//                                    countMap.put(word, c);
//                                }
//                            }
//                        }
//
//                        /** retrieves all the words and their counts, make a String */
//                        StringBuilder sb = new StringBuilder();
//                        Iterator<String> itr = countMap.keySet().iterator();
//                        while (itr.hasNext()) {
//                            String word = itr.next();
//                            Integer count = countMap.get(word);
//                            /** format is like  word1:count2|word2:count2|... */
//                            sb.append(word).append(":").append(count).append("|");
//                        }
//
//                        /** delete the last '|' **/
//                        sb.deleteCharAt(sb.length() - 1);
//
//                        WordCountObject wco =
//                                new WordCountObject(currentKey.split("#")[1]
//                                , currentKey.split("#")[0], sb.toString());
//                        result.add(wco);
//
//                        currentKey = strs[0];// make new currentKey.
//                        tmpList.clear();//clear the tmpList.
//                        WordCountObject w = new WordCountObject(uid, time, value);
//                        tmpList.add(w); // add current value to the tmplist
//
//                    } else {
//                        // still the same time. put the object into the list
//                        WordCountObject wco = new WordCountObject(uid, time, value);
//                        tmpList.add(wco);
//                    }
//                }
//            }
//
//            if (currentKey != null) {
//                /** counts all words' count **/
//                Map<String, Integer> countMap = new HashMap<String, Integer>(); // counts the word
//                for (WordCountObject wco : tmpList) {
//                    for (String str : wco.getWordcount().split("\\|")) { // for every word-count pair
//                        String word = str.split(":")[0];
//                        Integer c = Integer.parseInt(str.split(":")[1]);
//                        if (countMap.containsKey(word)) { // for every word
//                            Integer count = countMap.get(word);
//                            count += c; // gets the count number
//                            countMap.put(word, count);
//                        } else {
//                            countMap.put(word, c);
//                        }
//                    }
//                }
//
//                /** retrieves all the words and their counts, make a string */
//                StringBuilder sb = new StringBuilder();
//                Iterator<String> itr = countMap.keySet().iterator();
//                while (itr.hasNext()) {
//                    String word = itr.next();
//                    Integer count = countMap.get(word);
//                    /** format is like  word1:count2|word2:count2|... */
//                    sb.append(word).append(":").append(count).append("|");
//                }
//                /** delete the last | **/
//                sb.deleteCharAt(sb.length() - 1);
//
//                WordCountObject wco =
//                        new WordCountObject(currentKey.split("#")[1]
//                                , currentKey.split("#")[0], sb.toString());
//                result.add(wco);
//            }
//
//            Collections.sort(result);
//
//            for (WordCountObject wco : result) {
//                String[] strs = wco.getTime().split("-");
//                System.out.println(strs[0]+strs[1]+strs[2] + "\t" + wco.getUid() + "\t" + wco.getWordcount());
//            }
//
//        } catch(IOException io) {
//            io.printStackTrace();
//        }
//    }
//}
//
////class WordCountObject implements Comparable<WordCountObject> {
////    private String uid;
////    private String time;
////    private String wordcount;
////
////    public WordCountObject(String id, String time, String wordcount) {
////        this.uid = id;
////        this.time = time;
////        this.wordcount = wordcount;
////    }
////
////    public String getUid() {
////        return uid;
////    }
////
////    public void setUid(String uid) {
////        this.uid = uid;
////    }
////
////    public String getTime() {
////        return time;
////    }
////
////    public void setTime(String time) {
////        this.time = time;
////    }
////
////    public String getWordcount() {
////        return wordcount;
////    }
////
////    public void setWordcount(String wordcount) {
////        this.wordcount = wordcount;
////    }
////
////    /**
////     * This method implements sorting function.
////     * Sort wcobjects by time in ascending order
////     * If tie exists in time, then sort by their uid in ascending order, numecially.
////     *
////     * @param o
////     * @return
////     */
////    @Override
////    public int compareTo(WordCountObject o) {
////        if (this.time.equals(o.time)) {
////            Long thisid = Long.parseLong(this.uid);
////            Long oid = Long.parseLong(o.uid);
////            return thisid.compareTo(oid);
////        }
////        return this.time.compareTo(o.time);
////    }
////}
