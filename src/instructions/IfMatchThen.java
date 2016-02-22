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

import MARC.Content;
import MARC.DirectoryEntry;
import MARC.DirtyRecord;
import MARC.Record;
import java.util.ArrayList;
import java.util.List;

/**
 * Does an if then using a regex. The character position in the if statement doesn't matter. 
 * TODO implement in Parser, test.
 * @author anisbet
 */
public class IfMatchThen extends Instruction
{

    private final List<Instruction> trueInstructions;
    private final String leftSide;
    private final String regex;
    private Record record;
    /**
     * Syntax: <tag> if match <regex> then <other command>
     * @param tokens tokens from the command file.
     * @throws IndexOutOfBoundsException
     * @throws instructions.Parser.SyntaxError 
     */
    public IfMatchThen(List<String> tokens) throws IndexOutOfBoundsException, Parser.SyntaxError
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
        this.leftSide = tokens.remove(0);
        // This is the regex
        this.regex  = tokens.remove(0);
        // Next token must be 'then'. this signals the collection of the rest of the line as an instruction.
        if (tokens.remove(0).compareToIgnoreCase("then") != 0) 
        {
            throw new Parser.SyntaxError(String.format("** error, missing 'then' clause.\n"));
        }
        Parser parser   = new Parser(true);
        // create new list for partial raw instrctions, that is, instruction that haven't been parsed yet.
        List<String> thisInstruction = new ArrayList<>();
        tokens.stream().forEach((iString) -> 
        {
            thisInstruction.add(iString);
        });
        Instruction nextInstruction = parser.getInstruction(thisInstruction);
        this.trueInstructions.add(nextInstruction);
    }
    
    @Override
    public boolean run() 
    {
        // get the content of the tag and check for the value at the appointed test position.
        List<DirectoryEntry> myTagEntries = this.record.getTags(this.tag);
        boolean result = false;
        for (DirectoryEntry de: myTagEntries)
        {
            // find the value at the requested position of any of the tags .
            Content content = de.getContent();
            if (content.toString().matches(this.regex))
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

    @Override
    public void setRecord(Record record) 
    {
        this.record = record;
    }
}
