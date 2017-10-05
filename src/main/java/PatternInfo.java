import java.util.ArrayList;
import java.util.List;

/**
 * Created by robotcator on 9/30/17.
 */
public class PatternInfo {
    int count;
    float freq;
    List<Integer> rootOccurence;

    public PatternInfo() {
        count = 0;
        rootOccurence = new ArrayList<Integer>();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Integer> getRootOccurence() {
        return rootOccurence;
    }

    public void setRootOccurence(List<Integer> rootOccurence) {
        this.rootOccurence = rootOccurence;
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
