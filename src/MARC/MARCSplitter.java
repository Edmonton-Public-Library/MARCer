package MARC;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Split the marc file. The splitter needs to take the stream find the leader,
 * the directory and data segments.
 * @author anisbet
 */
public class MARCSplitter
{
    private static List<Record> marcRecords;
    private static boolean isOutputIfModifiedOnly;
    
    /**
     * Create a record object that can tell if it has been modified.
     * @param fileName name of the MARC file to read.
     * @param modify sets if the object returned is a Record or DirtyRecord.
     */
    public MARCSplitter(String fileName, boolean modify)
    {
        marcRecords = new ArrayList<>();
        isOutputIfModifiedOnly = modify;
        init(fileName);
    }
    
    /**
     * Creates a standard set of MARC records based on the input file named.
     * @param fileName name of the MARC file.
     */
    public MARCSplitter(String fileName)
    {
        marcRecords = new ArrayList<>();
        isOutputIfModifiedOnly = false;
        init(fileName);
    }
        
    /**
     * Standard setup for reading records.
     * @param fileName name of MARC file.
     */
    private static void init(String fileName)
    {
        File f = new File(fileName);
        if (! f.isFile() )
        {
            throw new IllegalArgumentException(String.format("**Error: File '%s' doesn't exist.", fileName));
        }
        
        if (f.length() == 0)
        {
            throw new IllegalArgumentException(String.format("**Error: File '%s' has zero size.", fileName));
        }

        try
        {
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            int recordCount    = 0;
            int damagedRecords = 0;
            while (true)
            {
                Record record = readMARCRecord(in);
                if (record == null)
                {
                    break;
                }
                if (record.containsDamagedCharacters())
                {
                    damagedRecords++;
                    System.out.println(String.format("%15s", record.getTag("035")));
                }
                recordCount++;
                marcRecords.add(record);
            }
            String fString = String.format("%10s %7d\n%10s %7d", 
                "damaged:", damagedRecords, "total:", recordCount);
            System.out.println(fString);
            
            in.close();
        }
        catch (UnsupportedEncodingException e)
        {
           System.out.println(e.getMessage());
        } 
        catch (IOException e)
        {
           System.out.println(e.getMessage());
        }
    }
    
    private static Record readMARCRecord(DataInputStream stream) throws IOException   
    {
        byte[] leaderArray = new byte[Leader.LENGTH];
        // read All bytes of File stream
        if (stream.read(leaderArray, 0, Leader.LENGTH) <= 0) return null;
        Leader leader = new Leader(leaderArray);
        // create a buffer big enough for the rest of the record. We have already
        // read in the leader (24 bytes) so exclude that.
        byte[] remainderOfRecord = new byte[leader.getRecordLength() -Leader.LENGTH];
        if (stream.read(remainderOfRecord, 0, remainderOfRecord.length) <= 0) return null;
        Record record;
        if (isOutputIfModifiedOnly)
        {
            record = new DirtyRecord(leader, remainderOfRecord);
        }
        else
        {
            record = new Record(leader, remainderOfRecord);
        }
        return record;    
    }

    /**
     * Gets the records from the MARC file.
     * @return marc records in as Iterable.
     */
    public Iterable<Record> getRecords() 
    {
        return this.marcRecords;
    }
}
