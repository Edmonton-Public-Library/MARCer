/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MARC;

import java.util.Arrays;

/**
 * Represents a MARC leader. Can answer questions about a MARC record.
 * @author anisbet
 */
public class Leader
{
    //00-04 - Record length 
    //05 - Record status 
    //c - Corrected or revised 
    //d - Deleted 
    //n - New 
    //
    //06 - Type of record 
    //u - Unknown 
    //v - Multipart item holdings 
    //x - Single-part item holdings 
    //y - Serial item holdings 
    //
    //07-08 - Undefined character positions 
    //# - Undefined 
    //
    //09 - Character coding scheme 
    //# - MARC-8 
    //a - UCS/Unicode 
    //
    //10 - Indicator count 
    //2 - Number of character positions used for indicators 
    //
    //11 - Subfield code length 
    //2 - Number of character positions used for a subfield code 
    //
    //12-16 - Base address of data 
    //[number] - Length of Leader and Directory
    //        
    //17 - Encoding level 
    //1 - Holdings level 1 
    //2 - Holdings level 2 
    //3 - Holdings level 3 
    //4 - Holdings level 4 
    //5 - Holdings level 4 with piece designation 
    //m - Mixed level 
    //u - Unknown 
    //z - Other level 
    //
    //18 - Item information in record 
    //i - Item information 
    //n - No item information 
    //
    //19 - Undefined character position 
    //# - Undefined 
    //
    //20 - Length of the length-of-field portion 
    //4 - Number of characters in the length-of-field portion of a Directory entry 
    //
    //21 - Length of the starting-character-position portion 
    //5 - Number of characters in the starting-character-position portion of a Directory entry 
    //
    //22 - Length of the implementation-defined portion 
    //0 - Number of characters in the implementation-defined portion of a Directory entry 
    //
    //23 - Undefined 
    //0 - Undefined
        
    public final static int LENGTH = 24;
    public final static String TAG = "LDR";
    //00-04	Logical record length
    private int logicalRecordLength;
    //12-16	Base address of data Length of Leader and Directory
    private int baseAddressOfData;
    private byte[] leader;
    
    public Leader(byte[] leaderBytes)
    {
        if (leaderBytes.length < Leader.LENGTH)
        {
            throw new IllegalArgumentException("** error invalid leader length.");
        }
        this.leader = Arrays.copyOfRange(leaderBytes, 0, leaderBytes.length);
        //00-04	Logical record length
        String stringVersion = Utility.getByteRange(this.leader, 0, 5);
        try
        {
            this.logicalRecordLength = Integer.parseInt(stringVersion);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("** error couldn't read leader length.");
        }
        // 12-16	Base address of data Length of Leader and Directory
        stringVersion = Utility.getByteRange(this.leader, 12, 17);
        this.baseAddressOfData = Integer.parseInt(stringVersion);
        // 20-23	Entry map
        // always the same 4500.
    }
    
    public int getRecordLength()
    {
        return this.logicalRecordLength;
    }
    
    /**
     * @param logicalRecordLength the logicalRecordLength in bytes.
     */
    public void setLogicalRecordLength(int logicalRecordLength)
    {
        if (logicalRecordLength > 99999)
        {
            throw new IllegalArgumentException("** error, logical record length too long: '"
            + logicalRecordLength + "'");
        }
        this.logicalRecordLength = logicalRecordLength;
        String fString = String.format("%05d", this.logicalRecordLength);
        String[] ss = fString.split("|");
        int count = 0;
        for (String s: ss)
        {
            if (s.isEmpty() == false)
            {
                this.leader[count] = (byte)s.charAt(0);
                count++;
            }
        }
    }
    
    /**
     * 
     * @return true if MARC record is MARC-8 encoding, and false otherwise.
     */
    public boolean isMARC8Encoding()
    {
        return (char)this.leader[9] == 0x20; // ' '
    }
    
    /**
     * 
     * @return true if MARC record is UCS/Unicode encoding, and false otherwise.
     */
    public boolean isUTF8Encoding()
    {
        return this.leader[9] == 0x61; // 'a'
    }
    
    /**
     * Sets the UTF8 encoding position in the leader.
     */
    public void setUTF8EncodingFlag()
    {
        this.leader[9] = 0x61; // 'a'
    }
    
    /**
     * Tests if a specific position in the leader is set to the argument value.
     * @param position - 0 indexed position of leader character to test.
     * @param testValue character value to test against.
     * @return True if the position in the leader matches the test value 
     * argument and false otherwise.
     */
    public boolean testPosition(int position, char testValue)
    {
        if (position >= Leader.LENGTH) return false;
        return this.leader[position] == (byte)testValue;
    }
    
    public void setMARC8EncodingFlag()
    {
        this.leader[9] = 0x20; // ' '
    }
     
    /**
     * @return the leader
     */
    public byte[] getLeaderBytes()
    {
        return leader;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("=")
                .append(Leader.TAG)
                .append("  ")
                .append(String.format("%s", Utility.getString(this.leader)));
        return sb.toString();
    }
}
