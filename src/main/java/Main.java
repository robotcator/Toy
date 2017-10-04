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

    void FindRootOcc(SweepBranchStack B, Triple t) {
        
    }

    public static List<Triple> UpdateB(SweepBranchStack B, Map<String, PatternInfo> C, int depth, String label, int time) {
        int top = B.top;
        if (depth <= top) {

        }
        List<Triple> exp = new ArrayList<Triple>();
        exp.add(new Triple(label, depth, depth));

        if (depth - 1 >= 0) {
            SweepBranch d_1 = B.SB.get(depth-1);
            for (Iterator<Triple> it = d_1.B.iterator(); it.hasNext(); ) {

            }
        }
        return exp;
    }

    public static Map<String, PatternInfo> UpdateC(List<Triple> exp, SweepBranchStack B, Map<String, PatternInfo> C,
                               int depth, String label, int time) {

        // Increment candidates
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {

        }

        // Delete candidates
        for (Iterator<Map.Entry<String, PatternInfo>> it = C.entrySet().iterator(); it.hasNext(); ) {

        }
        // Insert candidates in B[d]
        for (Iterator<Triple> it = exp.iterator(); it.hasNext(); ) {

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

                SweepBranchStack SB = new SweepBranchStack();
                Map<String, PatternInfo> C = new HashMap<String, PatternInfo>();

                UpdateB(SB, C, depth, label, time);

                time += 1;
            }

            bfReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
