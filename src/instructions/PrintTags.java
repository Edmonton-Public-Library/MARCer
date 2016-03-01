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

import MARC.DirectoryEntry;
import MARC.Record;
import instructions.Parser.SyntaxError;
import java.util.List;

/**
 * Syntax: '[tag|leader] print 
 * @author anisbet
 */
public class PrintTags extends Instruction
{
    protected enum PrintType
    {
        record,
        leader,
        tag
    }
    protected Record record;
    protected PrintType printType;
    
    PrintTags(List<String> tokens) throws SyntaxError 
    {   
        // [tag|record|leader]
        this.tag  = tokens.remove(0);
        this.verb = tokens.remove(0);
        switch (this.verb)
        {
            case "print":
                break;
            default:
                throw new SyntaxError(String.format("operation '%s' not supported.", this.verb));
        }
        switch (this.tag)
        {
            case "record":
                this.printType = PrintType.record;
                break;
            case "leader":
            case "LDR":
                this.printType = PrintType.leader;
                break;
            default:
                this.printType = PrintType.tag;
                break;
        }
    }

    @Override
    public void setRecord(Record record) 
    {
        this.record = record;
    }

    @Override
    public boolean run() 
    {
        String output;
        switch (printType)
        {
            case record:
                output = this.record.toString();
                break;
            case leader:
                output = this.record.getLeader().getLeaderString();
                break;
            default:
                StringBuilder sb = new StringBuilder();
                for (DirectoryEntry de: this.record.getTags(this.tag))
                {
                    sb.append(de.getContent().toString()).append("\n");
                }
                output = sb.toString().trim(); // remove last new line.
                break;
        }
        System.out.printf("%s\n", output);
        Instruction.printedRecords++;
        return true;
    }
}
