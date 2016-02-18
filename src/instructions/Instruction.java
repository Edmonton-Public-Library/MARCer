/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructions;

import MARC.Record;

/**
 * Generic instruction that operates or tests some aspect of a MARC file.
 * @author anisbet
 */
public abstract class Instruction 
{
    protected String tag;
    protected String verb;
    protected static boolean debug = false;
    protected static boolean ignoreIndicators = false;
    
    /**
     * Turns on debugging information.
     * @param debug turns on debugging.
     */
    public static void setDebug(boolean debug)
    {
        Instruction.debug = debug;
    }
    
    /**
     * Sets the Directory of the MARC record, which is the payload of the MARC
     * file.
     * @param record Content of the MARC file.
     */
    public abstract void setRecord(Record record);

    /**
     * Does the modification or test.
     * @return true if the modification was performed or the test passed, and 
     * false otherwise.
     */
    public abstract boolean run();
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.tag).append("::").append(this.verb);
        return sb.toString();
    }

    /**
     * @return true if the finalization method was successful and false otherwise.
     * @see WriteFileN
     */
    public boolean setFinalize() 
    {
        return true;
    }
}
