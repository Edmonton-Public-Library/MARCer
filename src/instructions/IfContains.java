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
public class IfContains extends Instruction
{
    private final String regex;
    private Record record;
    private final List<Instruction> trueInstructions;
    private final List<Instruction> falseInstructions;
    
    /**
     * Syntax: [tag>] if content == [regex] then [other commands]; else [other commands];
     * @param tokens tokens from the command file.
     * @throws IndexOutOfBoundsException
     * @throws instructions.Parser.SyntaxError 
     */
    public IfContains(List<String> tokens) 
            throws IndexOutOfBoundsException, 
            Parser.SyntaxError
    {
        this.tag = tokens.remove(0);
        this.verb = tokens.remove(0);
        // Create new list for finished instructions.
        this.trueInstructions = new ArrayList<>();
        this.falseInstructions= new ArrayList<>();
        tokens.remove(0); // This is just the keyword 'content' which we test for in the super class.
        tokens.remove(0); // This is just the keyword '==' which we test for in the super class.
        // This is the regex
        this.regex = tokens.remove(0);
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
