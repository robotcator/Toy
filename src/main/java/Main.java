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

    public static void UpdateB(Stack B, Map<String, PatternInfo> C, int depth, String label, int time) {
        int top = B.size();
    }

    public static void UpdateC() {

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

                Stack SB = new Stack<SweepBranch>();
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
