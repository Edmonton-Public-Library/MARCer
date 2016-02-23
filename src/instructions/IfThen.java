/*
 * Copyright 2016 Andrew.
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
import instructions.Parser.SyntaxError;
import java.util.ArrayList;
import java.util.List;

/**
 * If then test of a field from a MARC record.
 * @author Andrew Nisbet
 */
public class IfThen extends Instruction
{

    protected Record record;
    protected List<Instruction> trueInstructions;
    protected int    leftSide;
    protected String operator;
    protected String rightSide;
    /**
     * Instruction looks like '008 if 1 == 5 then 035 print'
     * @param tokens tokens read from the instruction file.
     * @throws instructions.Parser.SyntaxError if the instruction is formulated
     * incorrectly.
     */
    public IfThen(List<String> tokens) throws
            IndexOutOfBoundsException,
            Parser.SyntaxError
    {
        this.tag = tokens.remove(0);
        this.verb= tokens.remove(0);
        switch (this.verb)
        {
            case "if":
                break;
            default:
                throw new Parser.SyntaxError(String.format("** error, unknown statement '%s'.\n", this.verb));
        }
        // Create new list for finished instructions.
        this.trueInstructions = new ArrayList<>();
        this.leftSide = Parser.isNumber(tokens.remove(0));
        this.operator = tokens.remove(0);
        switch (this.operator)
        {
            case "==":
                break;
            default:
                throw new SyntaxError(String.format("** error, unsupported operator '%s'.\n",this.operator));
        }
        // Character of any type
        this.rightSide  = tokens.remove(0);
        // Next token must be 'then'. this signals the collection of the rest of the line as an instruction.
        if (tokens.remove(0).compareToIgnoreCase("then") != 0) 
        {
            throw new SyntaxError(String.format("** error, missing 'then' clause.\n"));
        }
        Parser parser   = new Parser(true);
        // create new list for partial raw instrctions, that is, instruction that haven't been parsed yet.
        List<String> thisInstruction = new ArrayList<>();
        for (String iString: tokens)
        {
            thisInstruction.add(iString);
        }
        Instruction nextInstruction = parser.getInstruction(thisInstruction);
        this.trueInstructions.add(nextInstruction);
    }

    @Override
    public void setRecord(Record record) 
    {
        this.record = record;
    }

    @Override
    public boolean run() 
    {
        boolean result = false;
        if (this.tag.equalsIgnoreCase(Leader.TAG))
        {
            Leader l = this.record.getLeader();
            if (this.rightSide.charAt(0) == l.getLeaderString().charAt(this.leftSide))
            {
                result = true;
                // there may be no changes to the record, but we want the record to
                // be output if the user has selected so in the intructions.
                if (Instruction.writeChangedRecordsOnly && this.record instanceof DirtyRecord)
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
                    if (Instruction.writeChangedRecordsOnly && this.record instanceof DirtyRecord)
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
        return result;
    }
}
