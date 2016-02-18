/*
 * Metro allows customers from any affiliate library to join any other member library.
 *    Copyright (C) 2016  Edmonton Public Library
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 *
 */
package marcer;

import instructions.Instruction;
import instructions.Parser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private static final String VERSION = "0.01";

    public static void main(String[] args)
    {
        System.out.println("Running......");
        // parse the command line.
        MARCFile marcFile = null;
        List<Instruction> instructionList = new ArrayList<>();
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
                DEBUG = true;
            }
            // Handle the user instructions.
            if (cmd.hasOption("i"))
            {
                // read the [i]nstructions from a file and create modification instructions line-by-line.
                String instructionFile = cmd.getOptionValue("i");
                Parser parser = new Parser(DEBUG);
                if (! parser.parse(instructionFile, instructionList))
                {
                    System.err.println(
                            String.format("** error parsing instructions in file '%s'.",
                                    instructionFile));
                    System.exit(4);
                }
            }
            // Now handle the marc file reading to ensure that variables in instructions
            // are honoured during reading of the MARC file.
            if (cmd.hasOption("f"))
            {
                String marcFilePath = cmd.getOptionValue("f");
                if (Instruction.isOutputOnChangeOnly())
                {
                    marcFile = new MARCFile(marcFilePath, DEBUG, true);
                }
                else
                {
                    marcFile = new MARCFile(marcFilePath, DEBUG);
                }
            }
            else
            {
                System.err.println("Please specify a marc file on the command line.");
                System.exit(3);
            }
        } 
        catch (ParseException ex)
        {
            String msg = new Date() + "Unable to parse command line option. Please check configuration.";
            Logger.getLogger(MARCer.class.getName()).log(Level.SEVERE, msg, ex);
            System.exit(2);
        }
        // Don't run the instructions if there aren't any.
        if (instructionList.size() > 0)
        {
            marcFile.runInstructions(instructionList);
        }
        System.exit(0);
    }
    
}
