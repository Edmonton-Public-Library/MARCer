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

package utility;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Andrew Nisbet
 */
public class BinaryDiff
{
    
    public static final String VERSION = "0.1";
    
    public BinaryDiff(String[] args)
    {
        // First get the valid options
        Options options = new Options();
        // add x to specify input file.
        options.addOption("x", true, "file x directory path, sys dependant dir seperators like '/'.");
        // add y to specify input file.
        options.addOption("y", true, "file y directory path, sys dependant dir seperators like '/'.");
        // add t option c to config directory true=arg required.
        options.addOption("v", false, "Version information.");
        String fileName1 = "";
        String fileName2 = "";
        try
        {
            // parse the command line.
            CommandLineParser parser = new BasicParser();
            CommandLine cmd;
            cmd = parser.parse(options, args);
            if (cmd.hasOption("v"))
            {
                System.out.println("BinaryDiff version " + VERSION);
                System.exit(0);
            }
            // get the first file
            fileName1 = cmd.getOptionValue("x");
            fileName2 = cmd.getOptionValue("y");
        } 
        catch (ParseException ex)
        {
//            Logger.getLogger(MetroService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(new Date() + "**Error: Unable to parse command line option.");
            System.exit(100);
        }

        File f1 = new File(fileName1);
        if (! f1.isFile() )
        {
            throw new IllegalArgumentException("**Error: File x doesn't exist.");
        }
        
        if (f1.length() == 0)
        {
            throw new IllegalArgumentException("**Error: File x is zero size.");
        }
        File f2 = new File(fileName2);
        if (! f2.isFile() )
        {
            throw new IllegalArgumentException("**Error: File x doesn't exist.");
        }
        
        if (f2.length() == 0)
        {
            throw new IllegalArgumentException("**Error: File x is zero size.");
        }
        try
        {
            DataInputStream in2;
            try (DataInputStream in1 = new DataInputStream(new FileInputStream(f1))) {
                in2 = new DataInputStream(new FileInputStream(f2));
                int bufferSize = 2048;
                int bytes1Read = 0;
                int bytes2Read = 0;
                byte[] buffer  = new byte[bufferSize];
                byte[] buff2   = new byte[bufferSize];
                int diffCount = 0;
                OUTER: while (true)
                {
                    bytes1Read = in1.read(buffer, 0, bufferSize);
                    bytes2Read = in2.read(buff2, 0, bufferSize);
                    String fString;
                    for (int index = 0; index < bytes1Read; index++)
                    {
                        byte b1;
                        byte b2;
                        try 
                        {
                            b1 = buffer[index];
                            b2 = buff2[index];
                            if (b1 != b2)
                            {
                                fString = String.format("%4d): 0x%02x<=>0x%02x '%s'<=>'%s' ~", 
                                    index, b1, b2, (char)b1, (char)b2);
                                diffCount++;
                            }
                            else
                            {
                                fString = String.format("%4d): 0x%02x<=>0x%02x '%s'<=>'%s'", 
                                    index, b1, b2, (char)b1, (char)b2);
                            }
                            System.out.println(fString);
                        }
                        catch (ArrayIndexOutOfBoundsException e)
                        {
                            fString = String.format("%10s %7d position",
                                "differ at:", index);
                            System.out.println(fString);
                            break OUTER;
                        }
                    }
                    if (bytes1Read != bytes2Read)
                    {
                        fString = String.format("the files are different lengths\n%4s %7d\n%4s %7d\ndiffs: %7d",
                                "x:", bytes1Read, "y:", bytes2Read, diffCount);
                        System.out.println(fString);
                        break;
                    }
                    if (bytes1Read < 0 || bytes2Read < 0)
                    {
                        fString = String.format("EOF\n%4s EOF\n%4s EOF\ndiffs: %d",
                                "x:", "y:", diffCount);
                        System.out.println(fString);
                        break;
                    }
                }
            }
            in2.close();
        }
        catch (UnsupportedEncodingException e)
        {
           System.out.println(e.getMessage());
        } 
        catch (IOException e)
        {
           System.out.println(e.getMessage());
        }
//        String fString = String.format("%10s %7d\n%10s %7d", 
//                "damaged:", damagedRecords, "total:",totalRecords);
//        System.out.println(fString);
    }
}
