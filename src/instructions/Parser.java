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
package instructions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses instruction file into commands to be run against a given MARC file.
 * @author Andrew Nisbet
 */
public class Parser 
{    
    public final static String LEADER = "leader";
    private final boolean debug; 
    
    public Parser(boolean debug)
    {
        this.debug = debug;
    }
    
    public boolean parse(String file, List<Instruction> instructions)
    {
        // test if a real file
        File f = new File(file);
        if (! f.isFile())
        {
            System.err.print(String.format("File '%s' could not be found.\n", file));
            return false;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String instructionLine;
            int lineNumber = 0;
            OUTER: while ((instructionLine = br.readLine()) != null) 
            {
                lineNumber++;
                if (this.debug) System.out.printf("line # %3d) '%s'\n", lineNumber, instructionLine);
                instructionLine = instructionLine.trim();
                // Split the string into tokens.
                String[] tokens = instructionLine.split("\\s+");
                List<String> usefulTokens = new ArrayList<>();
                for (String token : tokens) 
                {
                    // Ignore everything after a comment char or REM statement for those that like syntax highlighting on cfg files.
                    if (token.startsWith("#")) continue OUTER;
                    if (token.compareToIgnoreCase("REM") == 0) continue OUTER;
                    if (token.isEmpty()) continue OUTER; // skip blank lines.
                    usefulTokens.add(token);
                }
                // To get here we have the syntax subject:verb:predicate.
                try
                {
                    Instruction instruction = getInstruction(usefulTokens);
                    instructions.add(instruction);
                }
                catch (SyntaxError e)
                {
                    System.err.printf("** Syntax error on line %d.\n", lineNumber);
                    System.exit(3);
                }
            }
        } 
        catch (IOException e) 
        {
            System.out.println(String.format("** error reading ''.", file));
            return false;
        }
        return true;
    }
    
    /**
     * Manufactures instruction object based on the verb of the sentence.
     * @param tokens tokens read from the instruction file.
     * @return Instruction.
     * @throws IndexOutOfBoundsException if the statement is incomplete.
     * @throws SyntaxError if instructions are malformed.
     */
    Instruction getInstruction(List<String> tokens) 
            throws IndexOutOfBoundsException, 
            SyntaxError
    {
        Instruction instruction;
        String verb    = tokens.get(1).toLowerCase();
        
        switch (verb) // the first token is the subject.
        {
            case "set":
                instruction = new SetPositionN(tokens);
                break;
            case "if":
                instruction = new IfThen(tokens);
                break;
            case "print":
                instruction = new PrintTags(tokens);
                break;
            case "var":
                instruction = new Variable(tokens);
                break;
            case "write":
                instruction = new WriteFile(tokens);
                break;
            case "pre-pend":
            case "append":
                instruction = new AppendTagN(tokens);
                break;
            case "add":
                instruction = new AddTag(tokens);
                break;
            case "delete":
                instruction = new DeleteTag(tokens);
                break;
            case "touch":
                instruction = new Touch(tokens);
                break;
            default:
                throw new UnsupportedOperationException(String.format("operation '%s' not supported.", verb));
        }
        Instruction.setDebug(debug);
        return instruction;
    }
    
    /**
     * Tests if the argument is a number.
     * @param n number to test.
     * @return true if the argument could be parsed to be an integer and false
     * otherwise.
     * @throws SyntaxError if instructions are malformed.
     */
    public static int isNumber(String n) throws SyntaxError
    {
        try
        {
            return Integer.parseInt(n.trim());
        }
        catch (NumberFormatException e)
        {
            throw new SyntaxError(
                    String.format("** error, expected an integer but got '%s'\n", n));
        }
    }

    /**
     * Thrown on the event of an error in syntax.
     */
    public static class SyntaxError extends Exception 
    {

        SyntaxError(int line, String msg) 
        {
            System.err.printf("** error on line %d, %s.\n", line, msg);
        }

        SyntaxError(String msg) 
        {
            System.err.printf("** error, %s.\n", msg);
        }
    }
}
