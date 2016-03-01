/*
 * Copyright 2016 anisbet.
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

import MARC.Record;
import instructions.Parser.SyntaxError;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * If then test of a field from a MARC record.
 * @author Andrew Nisbet
 */
public class If extends Instruction
{
    protected List<Instruction> trueInstructions;
    protected List<Instruction> falseInstructions;
    protected Instruction ifInstruction; // the sub instruction
    
    /**
     * Instruction looks like '008 if 1 == 5 then 035 print'
     * @param tokens tokens read from the instruction file.
     * @throws instructions.Parser.SyntaxError if the instruction is formulated
     * incorrectly.
     */
    public If(List<String> tokens) throws
            IndexOutOfBoundsException,
            Parser.SyntaxError
    {
        // Save the token strings, the sub classes reuse them.
        String ifType = tokens.get(2);
        // Test these next, the if operators remove them as they go so you can't access them after the objects are created.
        String comparisonOperator = tokens.get(3);
        switch (comparisonOperator)
        {
            case "==":
                break;
            default:
                throw new SyntaxError(String.format("** error, unsupported if variant '%s'.\n", comparisonOperator));
        }
        // Next token must be 'then'. this signals the collection of the rest of the line as an instruction.
        if (tokens.get(5).compareToIgnoreCase("then") != 0) 
        {
            throw new SyntaxError(String.format("** error, missing 'then' clause.\n"));
        }
        if (ifType.equalsIgnoreCase("content"))
        {
            this.ifInstruction = If.getInstance(ifType, tokens);
        }
        else
        {
            try
            {
                Integer.parseInt(ifType);
            }
            catch (NumberFormatException e)
            {
                throw new SyntaxError(String.format("** error, unsupported if variant '%s'.\n", ifType));
            }
            this.ifInstruction = If.getInstance("position", tokens);
        }
        
    }
    
    private static Instruction getInstance(String ifType, List<String> tokens) 
            throws IndexOutOfBoundsException, SyntaxError
    {
        switch (ifType)
        {
            case "content":
                return new IfContains(tokens);
            case "position":
                return new IfPosition(tokens);
            default:
                System.err.printf("** error unknown 'if' type statement '%s'\n", ifType);
                return null;
        }
    }

    @Override
    public void setRecord(Record record) 
    {
        this.ifInstruction.setRecord(record);
    }

    @Override
    public boolean run() 
    {
        return this.ifInstruction.run(); // todo fix me.
    }
    
    /**
     * Parses and populates the then and else clauses.
     * @param remainingTokens everything after the 'then' keyword to the EOL of the if statement.
     */
    protected void readThenElse(List<String> remainingTokens)
    {
        StringBuilder sb = new StringBuilder();
        // This doesn't allow for quoted strings in then else clauses.
        for (String s: remainingTokens)
        {
            sb.append(s).append(" ");
        }
        // expects a string like 'token token token; else token token token;
        String[] thenElse = sb.toString().trim().split("else");
        String[] myThen = thenElse[0].split(";"); // each command is now a string.
        // Process the then/else commands.
        Parser parser = new Parser(true);
        // create new list for partial raw instrctions, that is, instruction that haven't been parsed yet.
        for (String cmdString: myThen)
        {
            try 
            {
                // Strings at this point can have 1 quoted string, each of which is a token
                List<String> quotedCommand = parser.readQuotedTokens(cmdString);
                Instruction instruction = parser.getInstruction(quotedCommand);
                this.trueInstructions.add(instruction);
            } 
            catch (IndexOutOfBoundsException | SyntaxError ex) 
            {
                Logger.getLogger(If.class.getName()).log(Level.SEVERE, "** error in then clause. Expected 'statement';\n", ex);
            }
        }
        // else, if there is one.
        if (thenElse.length < 2) return;
        String[] myElse = thenElse[1].split(";");
        for (String cmdString: myElse)
        {
            try 
            {
                List<String> quotedCommand = parser.readQuotedTokens(cmdString);
                Instruction instruction = parser.getInstruction(quotedCommand);
                this.falseInstructions.add(instruction);
            } 
            catch (IndexOutOfBoundsException | SyntaxError ex) 
            {
                Logger.getLogger(If.class.getName()).log(Level.SEVERE, "** error in then clause. Expected 'statement';\n", ex);
            }
        }
    }
}

