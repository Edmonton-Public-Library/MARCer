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

/**
 * Removes named tag from the MARC record. Uses syntax '[tag] delete [which]?'. If there
 * are multiple tags, say 035 for example, which can be used to remove tags that 
 * match a pattern testing criteria TBD, otherwise all tags are removed.
 * @author anisbet
 */
public class DeleteTag extends Instruction
{
    private String matchString;
    private Record record;
    public DeleteTag(List<String> tokens) throws SyntaxError 
    {
        this.matchString = "";
        this.tag  = tokens.remove(0);
        this.verb = tokens.remove(0);
        switch (this.verb)
        {
            case "delete":
                break;
            default:
                throw new UnsupportedOperationException(String.format("operation '%s' not supported.", this.verb));
        }
        // optional parameters.
        if (tokens.size() < 1) return;
        String matchingWord = tokens.remove(0);
        if (matchingWord.compareToIgnoreCase("matching") == 0)
        {
            this.matchString = tokens.remove(0);
        }
        else
        {
            throw new SyntaxError(String.format("expected keyword 'matching', but got '%s'.", matchingWord));
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
        if (this.matchString.isEmpty())
        {
            return this.record.removeTags(this.tag);
        }
        return this.record.removeTags(this.tag, this.matchString);
    }
    
}
