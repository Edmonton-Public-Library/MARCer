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

import MARC.Record;
import instructions.Parser.SyntaxError;
import java.util.List;

/**
 * A modification to a MARC tag. Sets position 'n' of a given tag to a specified
 * value. Does not operate on leader objects. Syntax: 035 set 3 = p,
 * [tag] set [position] = [value]
 * @author anisbet
 */
class SetPositionN extends Instruction 
{
    private Record record;
    private Integer position;
    private final String newValue;
    private List<byte[]> entry;
    
    public SetPositionN(List<String> tokens) throws SyntaxError 
    {
        this.tag          = tokens.remove(0);
        this.verb         = tokens.remove(0);
        //                             6     =  o
        // the predicate looks like position = value
        this.position     = Integer.parseInt(tokens.remove(0));
        String assignment = tokens.remove(0);
        this.newValue     = tokens.remove(0);
        switch (assignment)
        {
            case "=":
                break;
            default:
                throw new SyntaxError(String.format("** error, unknown statement '%s'.\n", assignment));
        }
    }  

    @Override
    public boolean run() 
    {
        // Indicators -- The use of indicators is explained in fields where 
        // they are used. Indicators are one-digit numbers. Beginning with 
        // the 010 field, in every field -- following the tag -- are two 
        // character positions, one for Indicator 1 and one for Indicator 2. 
        // The indicators are not actually defined in all fields, however. 
        // And it is possible that a 2nd indicator will be used, while the 1st 
        // indicator remains undefined (or vice versa). When an indicator is 
        // undefined, the character position will be represented by the 
        // character # (for blank space).
        byte[] content = this.entry.remove(0);
        content[this.position] = this.newValue.getBytes()[0];
        this.entry.add(0, content);
        return this.record.setTagContent(tag, this.entry);
    }

    @Override
    public void setRecord(Record record) 
    {
        this.record = record;
        this.entry = this.record.getTagContent(tag);
    }  
}
