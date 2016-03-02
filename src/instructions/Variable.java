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
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Andrew
 */
class Variable extends Instruction 
{
    protected HashMap<String, String> symbolTable;
    
    public Variable(List<String> tokens) throws SyntaxError 
    {
        this.symbolTable     = new HashMap<>();
        this.tag             = tokens.remove(0);
        this.verb            = tokens.remove(0);
        String variableName  = tokens.remove(0);
        String assignment    = tokens.remove(0);
        if (assignment.compareTo("=") != 0)
        {
            throw new SyntaxError(String.format("** expected assignment char but got '%s'\n", assignment));
        }
        String variableValue = tokens.remove(0);
        switch (variableName)
        {
            case "debug":
                Environment.setDebug(Boolean.parseBoolean(variableValue));
                break;
            case "output_modified_only":
                Environment.setOutputOnChangeOnly(Boolean.parseBoolean(variableValue));
                break;
            case "strict":
                Environment.setStrict(Boolean.parseBoolean(variableValue));
                break;
            case "marc_file":
                Environment.setMarcFile(variableValue);
                break;
            default:
                this.symbolTable.put(variableName, variableValue);
                break;
        }
    }

    @Override
    public void setRecord(Record record) 
    {  }

    @Override
    public boolean run() 
    { 
        return true;
    }
}
