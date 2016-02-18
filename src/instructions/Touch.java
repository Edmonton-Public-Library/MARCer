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

import MARC.DirtyRecord;
import MARC.Record;
import java.util.List;

/**
 * Allows user to touch a file for output if a test succeeds.
 * @author Andrew Nisbet
 */
public class Touch extends Instruction
{
    private Record record;
    
    public Touch(List<String> tokens) 
    {
        this.tag = tokens.remove(0);
        this.verb = tokens.remove(0);
        switch (this.tag)
        {
            case "record":
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("%s doesn't support %s not supported.", 
                                this.verb, this.tag));
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
        if (Instruction.writeChangedRecordsOnly && this.record instanceof DirtyRecord)
        {
            // If no changes and doesn't match on say a filter selection skip it.
            ((DirtyRecord)this.record).touch();
            return true;
        }
        return false;
    }
}
