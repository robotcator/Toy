/**
 * Created by robotcator on 10/4/17.
 */
public class Pattern {
    String pattern;
    PatternInfo pinfo;

    public Pattern(String pattern, PatternInfo pinfo) {
        this.pattern = pattern;
        this.pinfo = pinfo;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "pattern='" + pattern + '\'' +
                ", pinfo=" + pinfo +
                '}';
    }
}
