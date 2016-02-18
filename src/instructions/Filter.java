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

import MARC.DirtyRecord;
import MARC.Record;
import java.util.List;

/**
 * Filter conditions on a record.
 * @author Andrew
 */
public class Filter extends Instruction
{
    private Record record;
    private String predicate;
    public Filter(List<String> tokens) 
    {
        this.tag = tokens.remove(0);
        this.verb = tokens.remove(0);
        this.predicate = tokens.remove(0);
        String filterType = this.tag;
        switch (filterType)
        {
            case "language":
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("%s doesn't support %s not supported.", 
                                this.verb, filterType));
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
        if (this.record.matchesLanguageIndicator(this.predicate))
        {
            // there may be no changes to the record, but we want the record to
            // be output if the user has selected so in the intructions.
            if (Instruction.writeChangedRecordsOnly && this.record instanceof DirtyRecord)
            {
                ((DirtyRecord)this.record).touch();
            }
            return true;
        }
        return false;
    }
    
}
