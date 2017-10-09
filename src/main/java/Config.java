import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by robotcator on 9/30/17.
 */
public class Config {
    static String input = "example.txt";
    static double threshold = 0.0;
    static int NodeNum = 0;

    // store the streamline data
    static List<Pair<Integer, String>> v = new ArrayList<Pair<Integer, String>>();

//    static List<Pattern> F = new ArrayList<Pattern>();
    // store the frequent i-1 set
    static Map<String, PatternInfo> F = new HashMap<String, PatternInfo>();
}
