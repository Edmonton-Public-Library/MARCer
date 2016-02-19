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
import instructions.Parser.SyntaxError;
import java.util.List;

/**
 * LanguageFilter filters file for output. All records that match the language 
 * condition are output whether they contain changes or not.
 * @author Andrew Nisbet
 */
public class LanguageFilter extends Instruction
{
    private Record record;
    private String predicate;
    public LanguageFilter(List<String> tokens) throws SyntaxError 
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
        switch (this.verb)
        {
            case "filter":
                break;
            default:
                throw new SyntaxError(
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
        if (this.record instanceof DirtyRecord)
        {
            ((DirtyRecord)this.record).unTouch();
        }
        if (this.record.matchesLanguageIndicator(this.predicate))
        {
            if (this.record instanceof DirtyRecord)
            {
                ((DirtyRecord)this.record).touch();
            }
            return true;
        }
        return false;
    }
}
