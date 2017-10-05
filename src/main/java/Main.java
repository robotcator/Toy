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

    public static Pair<Integer, String> FindRootOcc(SweepBranchStack B, Triple t) {
        System.out.println("FindRootOcc: " + t + " " + B.SB.size());
        if (B.SB.size() == 0 || B.SB.size() <= t.root) {
            int time = 0;
            return Config.v.get(time);
        }else{
            int time = B.SB.get(t.root).time;
            Pair<Integer, String> p = Config.v.get(time);

            if (t.pat.pinfo.rootOccurence.size() > t.root
                    && t.pat.pinfo.rootOccurence.get(t.root) == p) {
                return new Pair<Integer, String>(-1, "Undefined");
            }else{
                if (t.pat.pinfo.rootOccurence.size() == 0 || t.pat.pinfo.rootOccurence.size() < t.root) {
                    t.pat.pinfo.rootOccurence.add(p);
                }else{
                    t.pat.pinfo.rootOccurence.set(t.root, p);
                }
                return p;
            }
        }
    }

    public static List<Triple> UpdateB(SweepBranchStack B, Map<Pattern, PatternInfo> C, int depth, String label, int time) {
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
                System.out.println("SweepBranch: " + item);
                if (item.B.size() > 0) { // do not iterator the none elements
                    for (Iterator<Triple> tit = item.B.iterator(); it.hasNext(); ) {
                        Triple triple = tit.next();
                        System.out.println("Below: " + triple);

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
                System.out.println("Change: " + triple);
                if (triple.bottom == depth-1) {
                    B.SB.get(depth-1).B.add(triple);
                }
            }

        }
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

    public static Pattern getPredecessor(Pattern pattern, Map<Pattern, PatternInfo> C) {
        String predecessor = pattern.pattern.substring(0, pattern.pattern.length()-2);
        if (predecessor.length() == 0) {
            // the predecessor is None
            return new Pattern("");
        }else{
            for (Iterator<Map.Entry<Pattern, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Pattern, PatternInfo> item = it.next();
                if (item.getKey().pattern.equals(predecessor)) {
                    return item.getKey();
                }
            }
        }
        return new Pattern("");
    }

    public static Map<Pattern, PatternInfo> UpdateC(List<Triple> exp, SweepBranchStack B, Map<Pattern, PatternInfo> C,
                               int depth, String label, int time) {

        // Increment candidates
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {
            Triple item = it.next();

            Pair<Integer, String> p = FindRootOcc(B, item);

            if (p.getKey() != -1) {
                if (C.containsKey(item.pat)) {
                    C.remove(item.pat); // remove and update
                    item.pat.pinfo.count += 1;
                    C.put(item.pat, item.pat.pinfo);
                }

                Pattern predecessor = getPredecessor(item.pat, C);
                if (!C.containsKey(item.pat) &&
                        predecessor.pattern.length() > 0 && // not None
                        predecessor.pinfo.freq > Config.threshhold) {
                    item.pat.pinfo.count = 1;
                    C.put(item.pat, item.pat.pinfo);

                }
            }
        }

        B.top = depth;
        if (B.SB.size() > depth) {
            B.SB.get(depth).clear();
        }else{
            B.SB.add(new SweepBranch());
        }
        B.SB.get(depth).time = time;

        // update the frequency
        for (Iterator<Map.Entry<Pattern, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Pattern, PatternInfo> entry = it.next();
            entry.getKey().pinfo.freq = entry.getKey().pinfo.count / time;
            // update the frequency in patternInfo
        }

        // Delete candidates
        for (Iterator<Map.Entry<Pattern, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Pattern, PatternInfo> entry = it.next();
            Pattern predecessor = getPredecessor(entry.getKey(), C);
            if (true) {

            }

        }

        // Insert candidates in B[d]
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {
            Triple item = it.next();
            if (C.containsKey(item.pat)) {
                B.SB.get(depth).B.add(item);
            }
        }

        return C;
    }
    
    public static void main(String args[]) throws Exception{
        try {
            File file = new File(Config.input);
            BufferedReader bfReader = new BufferedReader(new FileReader(file));

            int time = 1;
            // initialize SB = None, top = -1; i = 1;
            SweepBranchStack SB = new SweepBranchStack();

            Map<Pattern, PatternInfo> C = new HashMap<Pattern, PatternInfo>();
            // the class of all single node patterns, is there any better way to handle?
            C.put(new Pattern("0A"), new PatternInfo());
            C.put(new Pattern("0B"), new PatternInfo());
            C.put(new Pattern("0R"), new PatternInfo());

            String tempStr;
            while ((tempStr = bfReader.readLine()) != null) {
                System.out.println(tempStr);
                String[] tokens = tempStr.split(",");
                int depth = Integer.parseInt(tokens[0]);
                String label = tokens[1].trim();

                Config.v.add(new Pair<Integer, String>(depth, label));

                List<Triple> exp = UpdateB(SB, C, depth, label, time);

                UpdateC(exp, SB, C, depth, label, time);

                time += 1;
            }
            bfReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void test_function() {
        // test the Set's get function
    }
}
