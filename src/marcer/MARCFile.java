/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcer;

import instructions.Instruction;
import MARC.MARCSplitter;
import MARC.Record;
import java.util.List;

/**
 *
 * @author anisbet
 */
class MARCFile 
{
    private final MARCSplitter marcSplitter;
    private boolean debug;
    
    public MARCFile(String file, boolean debug)
    {
        this.marcSplitter = new MARCSplitter(file);
        this.debug        = debug;
    }

    /** Sets the instructions used to modify or test the marc file.
     * 
     * @param instructionList List of instructions, either test or modify.
     */
    void runInstructions(List<Instruction> instructionList) 
    {
        for (Record r: this.marcSplitter.getRecords())
        {
            for (Instruction i: instructionList)
            {
                i.setRecord(r);
                if (! i.run() && debug)
                {
                    System.out.printf("fail: '%s'\n", i.toString());
                }
            }
        }
        // pass 2 to finalize any instructions that run at the end.
        for (Record r: this.marcSplitter.getRecords())
        {
            for (Instruction i: instructionList)
            {
                if (! i.setFinalize() && debug)
                {
                    System.out.printf("failed to finalize object: '%s'\n", i.toString());
                }
            }
        }
    }
}
