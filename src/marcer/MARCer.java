/*
 * Copyright 2016 Andrew Nisbet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package marcer;

import MARC.MARCFile;
import instructions.Environment;
import instructions.Interpreter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This class runs the MARC transformation tools.
 * java -cp dist/marcer.jar marcer.MARCer
 * @author Andrew Nisbet 
 */
public class MARCer 
{
    private static boolean DEBUG = false;
    private static final String VERSION = "0.02.00";

    public static void main(String[] args)
    {
        System.out.println("Running...");
        // parse the command line.
        MARCFile marcFile = null;
        Interpreter interpreter = null;
        // First get the valid options
        Options options = new Options();
        // add t option c to config directory true=arg required.
        options.addOption("d", false, "Turn on debugging.");
        // add v option v for server version.
        options.addOption("v", false, "MARCer version information.");
        // Add -f, marc file to read.
        options.addOption("f", true, "MARC file to analyse.");
        // Add -i, marc file to read.
        options.addOption("i", true, "File of test and modification instructions.");
        try
        {
            CommandLineParser cmdLineParser = new BasicParser();
            CommandLine cmd;
            cmd = cmdLineParser.parse(options, args);
            if (cmd.hasOption("v"))
            {
                System.out.println("MARCer version " + MARCer.VERSION);
                System.exit(0); // don't run if user just wants version.
            }
            
            if (cmd.hasOption("d")) // debug.
            {
                Environment.setDebug(true);
            }
            // Handle the user instructions.
            if (cmd.hasOption("i"))
            {
                interpreter = new Interpreter(cmd.getOptionValue("i"));
            }
            else
            {
                String msg = new Date() + "No instructions specified.";
                Logger.getLogger(MARCer.class.getName()).log(Level.SEVERE, msg);
                System.exit(2);
            }
            // Now handle the marc file reading to ensure that variables in instructions
            // are honoured during reading of the MARC file.
            if (cmd.hasOption("f"))
            {
                Environment.setMarcFile(cmd.getOptionValue("f"));
            }
            // Else should be specified in the instruction file.
        } 
        catch (ParseException ex)
        {
            String msg = new Date() + "Unable to parse command line option. Please check configuration.";
            Logger.getLogger(MARCer.class.getName()).log(Level.SEVERE, msg, ex);
            System.exit(2);
        }
        interpreter.runInstructions();
    }
}
