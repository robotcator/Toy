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
        return new Pair<Integer, String>(-1, "Undefined");
    }

    public static List<Triple> UpdateB(SweepBranchStack B, Map<Pattern, PatternInfo> C, int depth, String label, int time) {
        int top = B.top;
        if (depth <= top) {

        }
        List<Triple> exp = new ArrayList<Triple>();
        exp.add(new Triple(new Pattern("0" + label), depth, depth));

        if (depth - 1 >= 0) {
            SweepBranch d_1 = B.SB.get(depth-1);
            for (Iterator<Triple> it = d_1.B.iterator(); it.hasNext(); ) {
                //  for each (S, r, d-1) in B[d-1]
                Triple item = it.next();
                System.out.println(item);
                String T = item.pat + String.valueOf(depth-item.root) + label;
                exp.add(new Triple(new Pattern(T), item.root, depth));
            }
        }
        System.out.println("updateB: " + exp);
        return exp;
    }

    public static Pattern getPredecessor(Pattern pattern, Map<Pattern, PatternInfo> C) {
        return new Pattern("");
    }

    public static Map<Pattern, PatternInfo> UpdateC(List<Triple> exp, SweepBranchStack B, Map<Pattern, PatternInfo> C,
                               int depth, String label, int time) {

        // Increment candidates
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {
            Triple item = it.next();

            Pair<Integer, String> p = FindRootOcc(B, item);

            if (p.getKey() == -1) {
                if (C.containsKey(item.pat)) {
                    item.pat.pinfo.count += 1;
                }
                Pattern predecessor = getPredecessor(item.pat, C);
                if (!C.containsKey(item.pat) && predecessor.pinfo.freq > Config.threshhold) {
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

            String tempStr;
            int time = 1;
            while ((tempStr = bfReader.readLine()) != null) {
                System.out.println(tempStr);
                String[] tokens = tempStr.split(",");
                int depth = Integer.parseInt(tokens[0]);
                String label = tokens[1];

                // initialize SB = None, top = -1; i = 1;
                SweepBranchStack SB = new SweepBranchStack();

                Map<Pattern, PatternInfo> C = new HashMap<Pattern, PatternInfo>();
                // the class of all single node patterns, is there any better way to handle?
                C.put(new Pattern("0A"), new PatternInfo());
                C.put(new Pattern("0B"), new PatternInfo());
                C.put(new Pattern("0R"), new PatternInfo());

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
