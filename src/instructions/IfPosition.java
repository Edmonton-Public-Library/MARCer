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

import MARC.Content;
import MARC.DirectoryEntry;
import MARC.DirtyRecord;
import MARC.Leader;
import MARC.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * If then test of a field from a MARC record.
 * @author Andrew Nisbet
 */
public class IfPosition extends Instruction
{
    private int    leftSide;
    private String rightSide;
    private Record record;
    private final List<Instruction> trueInstructions;
    private final List<Instruction> falseInstructions;

    /**
     * Instruction looks like '008 if 1 == 5 then 035 print'
     * @param tokens tokens read from the instruction file.
     * @throws instructions.Parser.SyntaxError if the instruction is formulated
     * incorrectly.
     */
    public IfPosition(List<String> tokens) throws
            IndexOutOfBoundsException,
            Parser.SyntaxError
    {
        
        // we don't have to check any of these because that was done in the super class.
        this.tag = tokens.remove(0);
        this.verb= tokens.remove(0);
        // Create new list for finished instructions.
        this.trueInstructions = new ArrayList<>();
        this.falseInstructions= new ArrayList<>();
        try
        {
            this.leftSide = Parser.isNumber(tokens.remove(0));
        }
        catch (Parser.SyntaxError ex)
        {
            Logger.getLogger(IfPosition.class.getName()).log(Level.SEVERE, "** error value couldn't be converted to expected integer.", ex);
        }
        tokens.remove(0); // remove the comparison operator, it was tested in the super class 'If'.
        // Character of any type
        this.rightSide  = tokens.remove(0);
        // Next token must be 'then'. this signals the collection of the rest of the line as an instruction.
        tokens.remove(0);
        StringBuilder sb = new StringBuilder();
        tokens.stream().forEach((s) ->
        {
            sb.append(s).append(" ");
        });
        String remainingTokens = sb.toString().trim();
        // now the string is just 'statement; statement else statement; statement'. 
        String[] thenElse = remainingTokens.split("else");
        if (thenElse.length < 1) // nothing after 'then'
        {
            throw new Parser.SyntaxError(String.format("** error, missing then clause.\n"));
        }
        Parser parser = new Parser(true);
        // Get the 'then' clause statements.
        for (String s: thenElse[0].split(";"))
        {
            List<String> command = Parser.readQuotedTokens(s.trim());
            Instruction instruction = parser.getInstruction(command);
            this.trueInstructions.add(instruction);
        }
        // if there is an 'else' clause get those instructions in a similar way.
        if (thenElse.length > 1)
        {
            for (String s: thenElse[1].split(";"))
            {
                List<String> command = Parser.readQuotedTokens(s.trim());
                Instruction instruction = parser.getInstruction(command);
                this.falseInstructions.add(instruction);
            }
        }
    }

    @Override
    public boolean run() 
    {
        boolean result = false;
        if (this.tag.equalsIgnoreCase(Leader.TAG))
        {
            Leader leader = this.record.getLeader();
            if (this.rightSide.charAt(0) == leader.getLeaderString().charAt(this.leftSide))
            {
                result = true;
                // there may be no changes to the record, but we want the record to
                // be output if the user has selected so in the intructions.
                if (Environment.isOutputOnChangeOnly() && this.record instanceof DirtyRecord)
                {
                    ((DirtyRecord)this.record).touch();
                }
            }
        }
        else
        {
            // get the content of the tag and check for the value at the appointed test position.
            List<DirectoryEntry> myTagEntries = this.record.getTags(this.tag);
            for (DirectoryEntry de: myTagEntries)
            {
                // find the value at the requested position of any of the tags .
                Content content = de.getContent();
                if (this.rightSide.charAt(0) == content.toString().charAt(this.leftSide))
                {
                    result = true;
                    // there may be no changes to the record, but we want the record to
                    // be output if the user has selected so in the intructions.
                    if (Environment.isOutputOnChangeOnly() && this.record instanceof DirtyRecord)
                    {
                        ((DirtyRecord)this.record).touch();
                    }
                    break; // At least one match found!
                }
            }
        }
        if (result)
        {
            for (Instruction instruction: this.trueInstructions)
            {
                instruction.setRecord(this.record);
                result = instruction.run();
            }
        }
        else
        {
            for (Instruction instruction: this.falseInstructions)
            {
                instruction.setRecord(this.record);
                result = instruction.run();
            }
        }
        return result;
    }

    @Override
    public void setRecord(Record record)
    {
        this.record = record;
    }
}
