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

import MARC.MARCFile;
import MARC.Record;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and executes instructions.
 * @author Andrew
 */
public class Interpreter 
{
    private final List<Instruction> instructionList;
    public Interpreter(String commandFile)
    {
        this.instructionList = new ArrayList<>();
        Parser parser = new Parser();
        if (! parser.parse(commandFile, this.instructionList))
        {
            System.err.println(
                    String.format("** error parsing instructions in file '%s'.",
                            commandFile));
            System.exit(4);
        }
    }
    
    /** Sets the instructions used to modify or test the marc file.
     */
    public void runInstructions() 
    {
        // Read through the instructions for pre-processor instructions.
        MARCFile marcFile = new MARCFile.Builder(Environment.getMarcFile())
            .debug(Environment.isDebug())
            .setOutputOnModifyOnly(Environment.isOutputOnChangeOnly())
            .setStrict(Environment.isStrict())
            .build();
        for (Record r: marcFile.getRecords())
        {
            for (Instruction i: instructionList)
            {
                i.setRecord(r);
                if (! i.run() && Environment.isDebug())
                {
                    System.out.printf("fail: '%s'\n", i.toString());
                }
            }
        }
        // pass 2 to finalize any instructions that run at the end.
        for (Record r: marcFile.getRecords())
        {
            for (Instruction i: instructionList)
            {
                if (! i.setFinalize() && Environment.isDebug())
                {
                    System.out.printf("failed to finalize object: '%s'\n", i.toString());
                }
            }
        }
        System.out.printf("Records printed %6d\n", Environment.getPrintedRecords());
        System.out.printf("Records written %6d\n", Environment.getWrittenRecords());
    }
}
