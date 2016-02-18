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

import MARC.Record;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Andrew
 */
class Variable extends Instruction 
{
    protected HashMap<String, String> symbolTable;
    
    public Variable(List<String> tokens) 
    {
        this.symbolTable     = new HashMap<>();
        this.tag             = tokens.remove(0);
        this.verb            = tokens.remove(0);
        String variableName  = tokens.remove(0);
        String assignment    = tokens.remove(0);
        String variableValue = tokens.remove(0);
        switch (variableName)
        {
            case "debug":
                Instruction.setDebug(Boolean.parseBoolean(variableValue));
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