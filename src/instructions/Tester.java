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
import java.util.List;

/**
 * Tester is the super class for all testers, but for now it just tests whether
 * URLs are working or not.
 * If there is one, the link is tested for a specific string
 * and if the returning page contains the string, the run method returns true, if
 * the record doesn't contain a URL, or the resource at the end of the URL doesn't
 * return the argument string, the run method returns false.
 * Each record is scanned for a 856 tag where the URL is expected, if there isn't
 * one it is reported. 
 * 
 * Syntax: 856 test_url "content test string" 
 * @author Andrew Nisbet
 */
class Tester extends Instruction
{
    private final Instruction instruction;
    private Record record;

    public Tester(List<String> tokens) throws Parser.SyntaxError
    {
        this.tag        = tokens.get(0);
        this.verb       = tokens.get(1);
        String testType = tokens.get(2);
        switch (testType)
        {
            case "url":
                this.instruction = new TestURL(tokens);
                break;
            default:
                throw new Parser.SyntaxError(String.format("** error, unknown statement '%s'.\n", testType));
        }
    }

    @Override
    public void setRecord(Record record)
    {
        this.record = record;
        this.instruction.setRecord(record);
    }

    @Override
    public boolean run()
    {
        return this.instruction.run();
    }
}
