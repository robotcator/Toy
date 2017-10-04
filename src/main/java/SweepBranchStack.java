import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SweepBranchStack {
    List<SweepBranch> SB;
    int top;

    public SweepBranchStack() {
        this.SB = new ArrayList<SweepBranch>();
        this.top = -1;
    }
}
