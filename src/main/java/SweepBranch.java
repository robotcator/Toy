import java.util.ArrayList;
import java.util.List;

/**
 * Created by robotcator on 9/30/17.
 */
public class SweepBranch {
    List<Triple> B;
    int time;

    public void setB(List<Triple> b) {
        B = b;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public SweepBranch() {
        this.B = new ArrayList<Triple>();
        this.time = 0;
    }

    public void clear() {
        this.time = 0;
        B.clear();
    }

    @Override
    public String toString() {
        return "SweepBranch{" +
                "B=" + B +
                ", time=" + time +
                '}';
    }
}
