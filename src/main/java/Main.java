/**
 * Created by robotcator on 9/30/17.
 */

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void CLI(String args[]) throws Exception{
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");

        options.addOption("i", "input", true, "input file");
        options.addOption("s", "support", true, "threshold" );

        CommandLine commandLine = parser.parse( options, args );

        if( commandLine.hasOption('h') ) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("Options", options);
            System.exit(0);
        }
        if( commandLine.hasOption('i') ) {
            Config.input = commandLine.getOptionValue("i");
        }
        if( commandLine.hasOption('s') ) {
            Config.threshold = Float.parseFloat(commandLine.getOptionValue("s"));
        }

        System.out.println("The input file: " + Config.input);
        System.out.println("The threshold: " + Config.threshold);
    }

    public static Pair<Integer, String> FindRootOcc(SweepBranchStack B, Triple t, Map<String, PatternInfo> C, int time)  {
        // Test
        int T = -1;
        if (B.SB.size() <= t.root) {
            T = time;
        }else{
            T = B.SB.get(t.root).time;
        }
        System.out.println("the time: " + T);
        Pair<Integer, String> p = Config.v.get(T);
        if (C.containsKey(t.pat.pattern)) {

        }else{

        }
        return new Pair<Integer, String>(0, "");
    }

    public static Pair<Integer, String> FindRootOcc(SweepBranchStack B, Triple t, Map<String, PatternInfo> C) {
        if (Config.verbose) {
            System.out.println("FindRootOcc: " + t + " " + B.SB.size());
            System.out.println("candidate: " + C);
        }
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

            if (Config.verbose) {
                System.out.println("Remove: " + Remove);
                System.out.println("Change: " + Change);
            }

            // Change the bottom occurrences of the triples
            for (Iterator<Triple> it = Change.iterator(); it.hasNext(); ) {
                Triple triple = it.next();
                triple.bottom = depth-1;
                B.SB.get(depth-1).B.add(triple);
            }
        }

        // single node tree with the label `l`
        List<Triple> exp = new ArrayList<Triple>();
        exp.add(new Triple(new Pattern("0" + label), depth, depth));

        if (depth - 1 >= 0) {
            SweepBranch d_1 = B.SB.get(depth-1);
            if (Config.verbose) System.out.println("SweepBranch:" + d_1);
            for (Iterator<Triple> it = d_1.B.iterator(); it.hasNext(); ) {
                //  for each (S, r, d-1) in B[d-1]
                Triple item = it.next();
                if (Config.verbose) System.out.println("expansion: " + item);
                String T = item.pat.pattern + String.valueOf(depth-item.root) + label;
                exp.add(new Triple(new Pattern(T), item.root, depth));
            }
        }
        if (Config.verbose) {
            if(depth-1 >= 0) System.out.println("B[d-1]: " + B.SB.get(depth-1));
        }
        System.out.println("updateB exp: " + exp);
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

    public void increaseFreq(Triple t, Map<String, PatternInfo> C, int time, boolean model, double gamma) {
        // model
        // false: online model
        // true: forgeting model
        if (model) {

        }else {
            if (C.containsKey(t.pat.pattern)) {
                C.get(t.pat.pattern).count += 1;
            }else{
                t.pat.pinfo.count = 1;
                C.put(t.pat.pattern, t.pat.pinfo);
            }
        }
    }

    public double getFreq(int time, boolean model, double gamma) {
        // model
        // false: online model
        // true: forgeting model
        if (model) {

        }else{

        }
        return 0;
    }

    public static Map<String, PatternInfo> UpdateC(List<Triple> exp, SweepBranchStack B, Map<String, PatternInfo> C,
                               int depth, String label, int time) {
        // Increment candidates
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {
            Triple item = it.next();

            Pair<Integer, String> p = FindRootOcc(B, item, C);
            if (Config.verbose) {
                System.out.println("RootOcc: " + p);
                System.out.println("Candidate update: " + C);
                System.out.println("Triple item: " + item);
            }

            if ( p.getKey() != -1 && (!p.getValue().equals("Undefine")) ) {
                if (C.containsKey(item.pat.pattern)) {

                    if (!Config.ForgetingModel) {
                        C.get(item.pat.pattern).count += 1;
                    }else{
                        // forgeting model
                        if (C.get(item.pat.pattern).count == 0) {
                            // for the pre-insert pattern sake
//                            C.get(item.pat.pattern).freq = 1.0 / time;

                            C.get(item.pat.pattern).first = time;
                            C.get(item.pat.pattern).last = time;
                        }else{
//                            PatternInfo Tmp = C.get(item.pat.pattern);
//                            double lastFreq = Tmp.freq * Tmp.last * Math.exp(time - Tmp.last) / (time-1);
//                            C.get(item.pat.pattern).freq = lastFreq + 1.0 / time;
                            C.get(item.pat.pattern).first = C.get(item.pat.pattern).last;
                            C.get(item.pat.pattern).last = time;
                        }
                        C.get(item.pat.pattern).count += 1;
                    }

                }

                Pattern predecessor = getPredecessor(item.pat.pattern, C);
                if (Config.verbose) System.out.println("predecessor: " + predecessor);

                if (!C.containsKey(item.pat.pattern) &&
                        predecessor.pattern.length() > 0 && // not None
                        predecessor.pinfo.freq >= Config.threshold) {

                    if (!Config.ForgetingModel) {
                        item.pat.pinfo.count = 1;
                        C.put(item.pat.pattern, item.pat.pinfo);
                    }else{

                        item.pat.pinfo.count = 1;
                        item.pat.pinfo.first = time;
                        item.pat.pinfo.last = time;
                        C.put(item.pat.pattern, item.pat.pinfo);

                    }
                }
            }
        }
        for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, PatternInfo> item = it.next();
            if (item.getValue().count > 0) {
                System.out.println(item.getKey() + " " + item.getValue().count);
            }
        }

        B.top = depth;
        // B[d] = None
        if (B.SB.size() > depth) {
            B.SB.get(depth).clear();
        }else{
            B.SB.add(new SweepBranch());
        }
        B.SB.get(depth).time = time;

        if (!Config.ForgetingModel) {
            // update the frequency
            for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, PatternInfo> entry = it.next();
                entry.getValue().freq = entry.getValue().count * 1.0 / time;
                // update the frequency in patternInfo
            }
            if (Config.verbose) System.out.println("after increment C: " + C);
        }else{
            System.out.println("debugging: forgeting model");

            for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, PatternInfo> entry = it.next();
//                System.out.println(entry.getKey() + " " + entry.getValue().count +
//                        " " + entry.getValue().first + " " + entry.getValue().last);
                if (entry.getValue().last == time) {
                    // hit at time i
                    if (entry.getValue().first == time) {
                        // hit at time i, but it is the first time
                        double lastFreq = entry.getValue().freq * entry.getValue().last * Math.exp(time - entry.getValue().last) / (time);
                        C.get(entry.getKey()).freq = lastFreq + 1.0 / time;
                    }else{
                        double lastFreq = entry.getValue().freq * entry.getValue().last * Math.exp(time - entry.getValue().last) / (time);
                        C.get(entry.getKey()).freq = lastFreq;
                    }
                }else{
                    // did not hit at time i
                    double lastFreq = entry.getValue().freq * entry.getValue().last * Math.exp(time - entry.getValue().last) / (time);
                    C.get(entry.getKey()).freq = lastFreq;
                }
                // update the frequency in patternInfo
            }

        }

        // Delete candidates
        for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, PatternInfo> entry = it.next();
            Pattern predecessor = getPredecessor(entry.getKey(), C);
            if (Config.verbose) System.out.println(predecessor);
            if (predecessor.pattern.length() == 0) {
                // do not have predecessor
            }else{
                // infrequent at time i and frequent at time i-1
                if (C.get(predecessor.pattern).freq < Config.threshold
                        && Config.F.containsKey(predecessor.pattern)) {
                    it.remove();
                }
            }
        }
        if (Config.verbose) System.out.println("after delete C: " + C);

        // Insert candidates in B[d]
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {
            Triple item = it.next();
            if (Config.verbose) {
                System.out.println("Insert candidates: " + item +
                        "Inserted(True/False): " + C.containsKey(item.pat.pattern));
            }
            if (C.containsKey(item.pat.pattern)) {
                B.SB.get(depth).B.add(item);
            }
        }

        System.out.println("Branch Stack(B[d]) :" + B.SB.get(depth));
        System.out.println("Candidate pool");
        System.out.println(C);
        return C;
    }

    public static void QueryFrequent(Map<String, PatternInfo> C, int time) {
        Config.F.clear();
        for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, PatternInfo> item = it.next();
            if (item.getValue().freq >= Config.threshold && item.getValue().count != 0) {
                Config.F.put(item.getKey(), item.getValue());
            }
        }
        System.out.println("Time: " + time + " Frequent Set");
        for (Iterator<Map.Entry<String, PatternInfo>> it = Config.F.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, PatternInfo> item = it.next();
            System.out.println(item.getKey() + " " + item.getValue());
        }
    }
    
    public static void main(String args[]) throws Exception{
        Config.v.add(new Pair<Integer, String>(0, ""));

        CLI(args);

        try {
            File file = new File(Config.input);
            BufferedReader bfReader = new BufferedReader(new FileReader(file));

            int time = 1;
            // initialize SB = None, top = -1; i = 1;
            SweepBranchStack SB = new SweepBranchStack();

            Map<String, PatternInfo> C = new TreeMap<String, PatternInfo>();
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

//                QueryFrequent(C, time);

                time += 1;
            }
            bfReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
