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
import java.util.List;
import utility.URLTester;

/**
 *
 * @author anisbet
 */
public class TestURL extends Instruction
{
    private final String predicate;
    private Record record;
    public TestURL(List<String> tokens) throws Parser.SyntaxError
    {
        this.tag        = tokens.remove(0);
        this.verb       = tokens.remove(0);
        String testType = tokens.remove(0);
        this.predicate  = tokens.remove(0).replace('"', ' ').trim();
    }

    @Override
    public void setRecord(Record record)
    {
        this.record = record;
    }

    @Override
    public boolean run()
    {
        List<DirectoryEntry> urls = this.record.getTags(this.tag);
        for (DirectoryEntry de: urls)
        {
            String url = de.getContent().extractSubfield('u');
            URLTester urlTester = new URLTester(url);
            // This is a little backwards, URL are expected to be valid if they
            // don't find the predicate in the page's content.
            if (! urlTester.search(this.predicate))
            {
                if (Environment.isOutputOnChangeOnly() && this.record instanceof DirtyRecord)
                {
                    // If no changes and doesn't match on say a filter selection skip it.
                    ((DirtyRecord)this.record).touch(); 
                }
                return true;
            }
        }
        System.err.printf("** URL test failed on TCN '%s'\n", this.record.getTCN());
        return false;
    }
}
