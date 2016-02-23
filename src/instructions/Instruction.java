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

    public static int getPrintedRecords()
    {
        return printedRecords;
    }

    public static int getWrittenRecords()
    {
        return writtenRecords;
    }
    
    protected String tag;
    protected String verb;
    protected static int printedRecords = 0;
    protected static int writtenRecords = 0;
    protected static boolean debug = false;
    protected static boolean ignoreIndicators = false;
    protected static boolean writeChangedRecordsOnly = false;
    protected static boolean isStrict = false;
    
    /**
     * Turns on debugging information.
     * @param debug turns on debugging.
     */
    static void setDebug(boolean debug)
    {
        Instruction.debug = debug;
    }
    
    /**
     * Reports the current setting of the strict flag.
     * @return the value of the strict flag.
     */
    public static boolean isStrict() 
    {
        return isStrict;
    }
    
    /**
     * Sets the strict-checking flag.
     * @param strict true to be strict about error and false otherwise.
     */
    static void setStrict(boolean strict)
    {
        isStrict = strict;
    }
    
    /**
     * Sets the application to output only changed records, or records that test 
     * successfully.
     * @param b true to get just changed records and false otherwise.
     */
    static void setOutputOnChangeOnly(boolean b)
    {
        Instruction.writeChangedRecordsOnly = b;
    }
    
    /** 
     * Tests if the flag to output changed records only is on.
     * @return true if user requests changes records only, and false otherwise.
     */
    public static boolean isOutputOnChangeOnly()
    {
        return writeChangedRecordsOnly;
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
