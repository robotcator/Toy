/**
 * Created by robotcator on 9/30/17.
 */

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    void CLI(String args[]) throws Exception{
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");

        options.addOption("i", "i", false, "input file");
        options.addOption("s", "support", false, "threshold" );

        CommandLine commandLine = parser.parse( options, args );

        if( commandLine.hasOption('h') ) {
            System.out.println( "Help Message");
            System.exit(0);
        }
        if( commandLine.hasOption('i') ) {
            Config.input = commandLine.getOptionValue('i');
        }
        if( commandLine.hasOption('s') ) {
            Config.threshhold = Float.parseFloat(commandLine.getOptionValue('s'));
        }
    }

    public static Pair<Integer, String> FindRootOcc(SweepBranchStack B, Triple t, Map<String, PatternInfo> C) {
        System.out.println("FindRootOcc: " + t + " " + B.SB.size());
        System.out.println("candidate: " + C);

        if (B.SB.size() == 0 || B.SB.size() <= t.root) {
            Pair<Integer, String> p = new Pair<Integer, String>(0, "");
            // B[r].time not define
            // update root occurrence

            while (t.pat.pinfo.rootOccurence.size() < t.root) {
                t.pat.pinfo.rootOccurence.add(new Pair<Integer, String>(-1, "Null"));
            }
            t.pat.pinfo.rootOccurence.add(p);

            return p;
        }else{
            int time = B.SB.get(t.root).time;
            Pair<Integer, String> p = Config.v.get(time);

            if (C.containsKey(t.pat.pattern)) {
                // exist pattern
                if (C.get(t.pat.pattern).rootOccurence.size() <= t.root) {
                    while(C.get(t.pat.pattern).rootOccurence.size() <= t.root) {
                        C.get(t.pat.pattern).rootOccurence.add(new Pair<Integer, String>(-1, "Null"));
                    }
                    C.get(t.pat.pattern).rootOccurence.add(p);
                    t.pat.setPinfo(C.get(t.pat.pattern));
                    return p;
                }else{
                    Pair<Integer, String> rootOcc = C.get(t.pat.pattern).rootOccurence.get(t.root);
                    if (rootOcc.equals(p)) {
                        return new Pair<Integer, String>(-1, "Undefine");
                    }else{
//                        C.get(t.pat.pattern).rootOccurence.get(t.root).setKey(p.key);
//                        C.get(t.pat.pattern).rootOccurence.get(t.root).setValue(p.value);
                        rootOcc.setKey(p.key);
                        rootOcc.setValue(p.value);
                        t.pat.setPinfo(C.get(t.pat.pattern));
                        return p;
                    }
                }

            }else{
                if (t.pat.pinfo.rootOccurence.size() <= t.root) {
                    while(t.pat.pinfo.rootOccurence.size() <= t.root) {
                        t.pat.pinfo.rootOccurence.add(new Pair<Integer, String>(-1, "Null"));
                    }
                    t.pat.pinfo.rootOccurence.add(p);
                    return p;
                }else{
                    Pair<Integer, String> rootOcc = t.pat.pinfo.rootOccurence.get(t.root);

                    if (rootOcc.equals(p)) {
                        return new Pair<Integer, String>(-1, "Undefine");
                    }else {
                        t.pat.pinfo.rootOccurence.get(t.root).setKey(p.key);
                        t.pat.pinfo.rootOccurence.get(t.root).setValue(p.value);
                        return p;
                    }
                }

            }
        }
    }

    public static List<Triple> UpdateB(SweepBranchStack B, Map<String, PatternInfo> C, int depth, String label, int time) {
        int top = B.top;
        if (depth <= top) {
            SweepBranchStack Below = new SweepBranchStack();
            for (int i = depth; i <= top; i ++) {
                 Below.SB.add(B.SB.get(i));
            }
            // Discard the triples below the branching point
            List<Triple> Remove = new ArrayList<Triple>();
            // Collect the bottom occurrence of the triples
            List<Triple> Change = new ArrayList<Triple>();

            for (Iterator<SweepBranch> it = Below.SB.iterator(); it.hasNext(); ) {
                SweepBranch item = it.next();
//                System.out.println("SweepBranch: " + item);
                if (item.B.size() > 0) { // do not iterator the none elements
                    for (Iterator<Triple> tit = item.B.iterator(); tit.hasNext(); ) {
                        Triple triple = tit.next();
//                        System.out.println("Below: " + triple);
                        if (triple.root >= depth) {
                            Remove.add(triple);
                        }

                        if (triple.root <= depth - 1) {
                            Change.add(triple);
                        }
                    }
                }
            }

            // Change the bottom occurrences of the triples
            for (Iterator<Triple> it = Change.iterator(); it.hasNext(); ) {
                Triple triple = it.next();
//                System.out.println("Change: " + triple);
                if (triple.bottom == depth-1) {
                    B.SB.get(depth-1).B.add(triple);
                }
            }
        }

        // single node tree with the label `l`
        List<Triple> exp = new ArrayList<Triple>();
        exp.add(new Triple(new Pattern("0" + label), depth, depth));

        if (depth - 1 >= 0) {
            SweepBranch d_1 = B.SB.get(depth-1);
            System.out.println("SweepBranch:" + d_1);
            for (Iterator<Triple> it = d_1.B.iterator(); it.hasNext(); ) {
                //  for each (S, r, d-1) in B[d-1]
                Triple item = it.next();
                System.out.println("expansion: " + item);
                String T = item.pat.pattern + String.valueOf(depth-item.root) + label;
                exp.add(new Triple(new Pattern(T), item.root, depth));
            }
        }
        System.out.println("updateB: " + exp);
        return exp;
    }

    public static Pattern getPredecessor(String pattern, Map<String, PatternInfo> C) {
        String predecessor = pattern.substring(0, pattern.length()-2);
        if (predecessor.length() == 0) {
            // the predecessor is None
            return new Pattern("");
        }else{
            return new Pattern(predecessor, C.get(predecessor));
        }
    }

    public static Map<String, PatternInfo> UpdateC(List<Triple> exp, SweepBranchStack B, Map<String, PatternInfo> C,
                               int depth, String label, int time) {
        // Increment candidates
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {
            Triple item = it.next();

            Pair<Integer, String> p = FindRootOcc(B, item, C);
            System.out.println("RootOcc: " + p);
            System.out.println("Candidate update: " + C);
            System.out.println("Triple item: " + item);

            if ( p.getKey() != -1 && (!p.getValue().equals("Undefine")) ) {
                if (C.containsKey(item.pat.pattern)) {
                    C.remove(item.pat.pattern); // remove and update
                    item.pat.pinfo.count += 1;
                    C.put(item.pat.pattern, item.pat.pinfo);
                }

                Pattern predecessor = getPredecessor(item.pat.pattern, C);

                System.out.println("predecessor: " + predecessor);
                if (!C.containsKey(item.pat.pattern) &&
                        predecessor.pattern.length() > 0 && // not None
                        predecessor.pinfo.freq >= Config.threshhold) {
                    item.pat.pinfo.count = 1;
                    C.put(item.pat.pattern, item.pat.pinfo);
                }
            }
        }
        System.out.println("after increment C: " + C);

        B.top = depth;
        // B[d] = None
        if (B.SB.size() > depth) {
            B.SB.get(depth).clear();
        }else{
            B.SB.add(new SweepBranch());
        }
        B.SB.get(depth).time = time;

        // update the frequency
        for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, PatternInfo> entry = it.next();
//            entry.getValue().freq = entry.getValue().count / time;
            entry.getValue().setFreq(entry.getValue().count / time);
            // update the frequency in patternInfo
        }

        // Delete candidates
        for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, PatternInfo> entry = it.next();
            Pattern predecessor = getPredecessor(entry.getKey(), C);
            if (predecessor.pattern.length() == 0) {
                // do not have predecessor
            }else{
                // infrequent at time i and frequent at time i-1
                if (C.get(predecessor.pattern).freq >= Config.threshhold
                        && Config.F.containsKey(predecessor.pattern)) {
                    it.remove();
                }
            }
        }

        // Insert candidates in B[d]
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {
            Triple item = it.next();
            System.out.println("Insert candidates: " + item + "Inserted(True/False): " + C.containsKey(item.pat.pattern));
            if (C.containsKey(item.pat.pattern)) {
                B.SB.get(depth).B.add(item);
            }
        }

        System.out.println("Branch Stack(B[d]) :" + B.SB.get(depth));
        return C;
    }

    public static void QueryFrequent(Map<String, PatternInfo> C) {
        Config.F.clear();
        for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, PatternInfo> item = it.next();
            if (item.getValue().freq >= Config.threshhold) {
                Config.F.put(item.getKey(), item.getValue());
            }
        }
    }
    
    public static void main(String args[]) throws Exception{
        Config.v.add(new Pair<Integer, String>(0, ""));

        try {
            File file = new File(Config.input);
            BufferedReader bfReader = new BufferedReader(new FileReader(file));

            int time = 1;
            // initialize SB = None, top = -1; i = 1;
            SweepBranchStack SB = new SweepBranchStack();

            Map<String, PatternInfo> C = new HashMap<String, PatternInfo>();
            // the class of all single node patterns, is there any better way to handle?
            C.put("0A", new PatternInfo());
            C.put("0B", new PatternInfo());
            C.put("0R", new PatternInfo());

            String tempStr;
            while ((tempStr = bfReader.readLine()) != null) {
                System.out.println(time + " " + tempStr);
                String[] tokens = tempStr.split(",");
                int depth = Integer.parseInt(tokens[0]);
                String label = tokens[1].trim();

                Config.v.add(new Pair<Integer, String>(depth, label));

                List<Triple> exp = UpdateB(SB, C, depth, label, time);

                UpdateC(exp, SB, C, depth, label, time);

                QueryFrequent(C);

                time += 1;
            }
            bfReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
