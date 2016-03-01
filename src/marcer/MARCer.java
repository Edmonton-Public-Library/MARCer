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

import MARC.MARCFile;
import instructions.Environment;
import instructions.Instruction;
import instructions.Interpreter;
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
