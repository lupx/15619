//import java.util.*;
//
//import org.json.simple.*;
//import org.json.simple.parser.JSONParser;
//import org.w3c.dom.css.ElementCSSInlineStyle;
//
//import java.awt.JobAttributes;
//import java.awt.geom.FlatteningPathIterator;
//import java.io.*;
//import java.math.RoundingMode;
//import java.nio.Buffer;
//import java.security.interfaces.RSAKey;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//public class TwitterFilter {
//	private static Map<String, Integer> wordScore = new HashMap<>();
//	private static Set<String> stopList = new HashSet<>();
//	private static Set<String> rot = new HashSet<>();
//	private static char[] alpha = new char[]  {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
//			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
//	private static class Response {
//		String sentimentDensity;
//		String cencoredText;
//		String tweetId;
//		String userId;
//		String createTime;
//		List<String> hashTag;
//		public Response(String sentimentDensity, String cencoredText, String tweetId, String userId, String createTime, List<String> hashTag) {
//			this.sentimentDensity = sentimentDensity;
//			this.cencoredText = cencoredText;
//			this.tweetId = tweetId;
//			this.userId = userId;
//			this.createTime = createTime;
//			this.hashTag = hashTag;
//		}
//	}
//	private static Response filterTweet(JSONObject jsonObject) throws ParseException {
//		String text = (String) jsonObject.get("text");
//		String[] textFields = text.split("[^a-zA-Z0-9]+");
//		int score = 0;
//		int stopCount = 0;
//		int wordCount = 0;
//		for (String string : textFields) {
//			//calculate the sentiment density
//			if (string.length() > 0) {
//				wordCount++;
//				String lowerString = string.toLowerCase();
//				if (wordScore.containsKey(lowerString)) {
//					score += wordScore.get(lowerString);
//				}
//				if (stopList.contains(lowerString)) {
//					stopCount++;
//				}
//			}
//			//cencor text
//			if (rot.contains(string.toLowerCase())) {
//				StringBuilder stringBuilder = new StringBuilder(string);
//				for (int i = 1; i < stringBuilder.length() - 1; i++) {
//					stringBuilder.setCharAt(i, '*');
//				}
//				text = text.replaceAll("(?<![a-zA-Z0-9])" + string + "(?![a-zA-Z0-9])", stringBuilder.toString());
//			}
//		}
//		int ewc = wordCount - stopCount;
//		double sDensity = 0;
//		if (ewc != 0) {
//			sDensity = ((double) score) / ((double) ewc);
//		}
//		DecimalFormat decimalFormat = new DecimalFormat("0.000");
//		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
//		//get sentiment density
//		String density = decimalFormat.format(sDensity);
//
//		//get cencored text
//		text = text.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r").replace("\"", "\\\"");
//
//		//get tweet id
//		String tweetId = (String) jsonObject.get("id_str");
//
//		//get create time
//		String createTime = (String) jsonObject.get("created_at");
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
//		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//		Date date = simpleDateFormat.parse(createTime);
//		simpleDateFormat.applyPattern("yyyy-MM-dd HH-mm-ss");
//		createTime = simpleDateFormat.format(date);
//
//		//get userid
//		JSONObject user = (JSONObject) jsonObject.get("user");
//		String userId = (String) user.get("id_str");
//
//		//get all hashtags
//		List<String> list = new LinkedList<>();
//		JSONObject entities = (JSONObject) jsonObject.get("entities");
//		if (entities.containsKey("hashtags")) {
//			JSONArray hashTags = (JSONArray) entities.get("hashtags");
//			if (hashTags.size() > 0) {
//				for (int i = 0; i < hashTags.size(); i++) {
//					JSONObject hashObject = (JSONObject) hashTags.get(i);
//					String hash = (String) hashObject.get("text");
//					list.add(hash);
//				}
//			}
//		}
//
//		return new Response(density, text, tweetId, userId, createTime, list);
//	}
//	private static void preLoad() throws IOException {
//		BufferedReader reader1 = new BufferedReader(new FileReader(new File("afinn.txt")));
//		String line = null;
//		String[] fields = null;
//		while ((line = reader1.readLine()) != null) {
//			fields = line.split("\\t");
//			wordScore.put(fields[0], Integer.parseInt(fields[1]));
//		}
//		reader1.close();
//		BufferedReader reader2 = new BufferedReader(new FileReader(new File("common-english-word.txt")));
//		while ((line = reader2.readLine()) != null) {
//			fields = line.split(",");
//			for (String s : fields) {
//				stopList.add(s);
//			}
//		}
//		reader2.close();
//		BufferedReader reader3 = new BufferedReader(new FileReader(new File("banned.txt")));
//		while ((line = reader3.readLine()) != null) {
//			StringBuilder stringBuilder = new StringBuilder();
//			for (int i = 0; i < line.length(); i++) {
//				if (line.charAt(i) >= 'a' && line.charAt(i) <= 'z') {
//					stringBuilder.append(alpha[(line.charAt(i) - 'a' + 13) % 26]);
//				} else {
//					stringBuilder.append(line.charAt(i));
//				}
//			}
//			rot.add(stringBuilder.toString());
//		}
//		reader3.close();
//	}
//	private static boolean malformedCheck(JSONObject jsonObject) {
//		/*if (!jsonObject.containsKey("id") || !jsonObject.containsKey("id_str")
//				|| jsonObject.get("id") == null || jsonObject.get("id_str") == null) {
//			System.out.println("No id");
//			return false;
//		}*/
//		if ((!jsonObject.containsKey("id") && !jsonObject.containsKey("id_str")) ||
//				(jsonObject.get("id") == null && jsonObject.get("id_str") == null)) {
//			return false;
//		}
//		if (!jsonObject.containsKey("created_at") || jsonObject.get("created_at") == null) {
//			System.out.println("No created_at");
//			return false;
//		}
//		if (!jsonObject.containsKey("entities") || jsonObject.get("entities") == null) {
//			System.out.println("No entities");
//			return false;
//		}
//		return true;
//	}
//	private static String hashTags(List<String> list) {
//		StringBuilder sBuilder = new StringBuilder();
//		for (int i = 0; i < list.size(); i++) {
//			if (i != list.size() - 1) {
//				sBuilder.append(list.get(i) + "\t");
//			} else {
//				sBuilder.append(list.get(i));
//			}
//		}
//		return sBuilder.toString();
//	}
//	private static void printResult(Response res, PrintStream printer) throws FileNotFoundException {
//		printer.print(res.tweetId + "\t");
//		printer.print(res.userId + "\t");
//		printer.print(res.createTime + "\t");
//		printer.print(res.sentimentDensity + "\t");
//		printer.print(res.cencoredText);
//		if (res.hashTag.size() > 0) {
//			printer.print("\t" + hashTags(res.hashTag));
//		}
//		printer.println();
//	}
//	public static void main(String[] args) {
//		try {
//			int count = 0;
//			preLoad();
//			BufferedReader reader = new BufferedReader(new FileReader(new File("part-00000")));
//			PrintStream printer = new PrintStream(new FileOutputStream(new File("my_result")));
//			String line = null;
//			JSONParser parser = new JSONParser();
//			Set<String> set = new HashSet<>();
//			while ((line = reader.readLine()) != null) {
//				Object obj;
//				try {
//					obj = parser.parse(line);
//				} catch (Exception e) {
//					e.printStackTrace();
//					System.out.println("Can not parse to json object");
//					continue;
//				}
//				JSONObject jsonObject = (JSONObject) obj;
//				if (!malformedCheck(jsonObject)) {
//					continue;
//				}
//				if (!set.contains((String) jsonObject.get("id_str"))) {
//					Response response = filterTweet(jsonObject);
//					printResult(response, printer);
//					set.add((String) jsonObject.get("id_str"));
//				}
//			}
//			reader.close();
//			printer.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
