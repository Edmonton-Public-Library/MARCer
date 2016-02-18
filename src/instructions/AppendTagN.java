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
import MARC.Utility;
import instructions.Parser.SyntaxError;
import java.util.ArrayList;

/**
 * Appends or pre-pends a string to a given tag data.
 * Used to add EZ-proxy URLs to a specific field.
 * Syntax: [tag] [append|pre-pend] [some string]
 * @author anisbet
 */
public class AppendTagN extends Instruction 
{

    private Record record;
    private String value;
    private boolean append;
    
    public AppendTagN(List<String> tokens) throws SyntaxError
    {

        this.tag      = tokens.remove(0);
        this.verb     = tokens.remove(0);
        StringBuilder sb = new StringBuilder();
        for (String token: tokens)
        {
            sb.append(token).append(" ");
        }
        this.value = sb.toString().trim();
        switch (this.verb)
        {
            case "append":
                this.append = true;
                break;
            case "pre-pend":
                this.append = false; // pre-pend
                break;
            default:
                throw new SyntaxError(String.format("invalid command '%s'.\n", this.verb));
        }
    }

    @Override
    public boolean run() 
    {
        List<byte[]> bTagContents = this.record.getTagContent(this.tag);
        List<byte[]> bNewTagContents = new ArrayList<>();
        byte[] bValue = this.value.getBytes();
        for (byte[] bArray: bTagContents)
        {
            byte[] finalBytes = new byte[bArray.length + bValue.length];
            if (this.append)
            {
                Utility.copy(finalBytes, 0, bArray);
                Utility.copy(finalBytes, bArray.length, bValue);
            }
            else
            {
                Utility.copy(finalBytes, 0, bValue);
                Utility.copy(finalBytes, bValue.length, bArray);
            }
            bNewTagContents.add(finalBytes);
        }
        return this.record.setTagContent(tag, bNewTagContents);
    }

    @Override
    public void setRecord(Record record) 
    {
        this.record = record;
    }
}
