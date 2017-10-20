import java.util.ArrayList;
import java.util.List;

/**
 * Created by robotcator on 9/30/17.
 */
public class PatternInfo {
    int count;
    double freq;
    List<Pair<Integer, String>> rootOccurence;

    int first;
    int last;

    public PatternInfo() {
        this.count = 0;
        this.rootOccurence = new ArrayList<Pair<Integer, String>>();
        first = last = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatternInfo that = (PatternInfo) o;

        if (count != that.count) return false;
        return rootOccurence != null ? rootOccurence.equals(that.rootOccurence) : that.rootOccurence == null;
    }

    @Override
    public int hashCode() {
        int result = count;
        result = 31 * result + (rootOccurence != null ? rootOccurence.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PatternInfo{" +
                "count=" + count +
                ", freq=" + freq +
                ", rootOccurence=" + rootOccurence +
                '}';
    }
}
