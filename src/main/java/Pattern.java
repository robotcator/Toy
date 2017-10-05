/**
 * Created by robotcator on 10/4/17.
 */
public class Pattern {
    String pattern;
    PatternInfo pinfo;

    public Pattern(String pattern) {
        this.pattern = pattern;
        pinfo = new PatternInfo();
    }

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

    // is this right?
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pattern pattern1 = (Pattern) o;

        if (pattern != null ? !pattern.equals(pattern1.pattern) : pattern1.pattern != null) return false;
        return pinfo != null ? pinfo.equals(pattern1.pinfo) : pattern1.pinfo == null;
    }

    @Override
    public int hashCode() {
        int result = pattern != null ? pattern.hashCode() : 0;
        result = 31 * result + (pinfo != null ? pinfo.hashCode() : 0);
        return result;
    }
}
