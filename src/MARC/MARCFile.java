/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MARC;

import instructions.Instruction;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anisbet
 */
public class MARCFile 
{
    private final MARCSplitter marcSplitter;
    private final boolean debug;
    private boolean isStrict;
    
    public static class Builder
    {
        private boolean debug;
        private boolean isStrict;
        private final String file;
        private boolean outputModifiedOnly;
        
        /**
         * Creates builder with minimum constructor arguments.
         *
         * @param file name of the MARC file.
         */
        public Builder(String file)
        {
            this.file = file;
        }

        /**
         * Allows the sip connection to be constructed with an institution id.
         *
         * @param b true if strict checking is to be used and false otherwise.
         * False will cause minor errors to be reported but won't stop the show.
         * @return Connection builder
         */
        public Builder setStrict(boolean b)
        {
            this.isStrict = b;
            return this;
        }
        
        /**
         * Sets the debug switch on or off.
         * @param b true for debugging and false otherwise.
         * @return builder
         */
        public Builder debug(boolean b)
        {
            this.debug = b;
            return this;
        }
        
        /**
         * Sets the output to only work for records that have been modified.
         * @param b true to restrict output to modified records only and false to 
         * output all records regardless.
         * @return builder object.
         */
        public Builder setOutputOnModifyOnly(boolean b)
        {
            this.outputModifiedOnly = b;
            return this;
        }

        /**
         * Builds the MARCFile.
         *
         * @return MARCFile builder.
         */
        public MARCFile build()
        {
            return new MARCFile(this);
        }
    }

    /**
     * Creates a SIPConnector but not without the help of a builder object.
     * Usage: SIPConnector c = SIPConnector.Builder(host, port).build();
     *
     * @param builder
     */
    private MARCFile(Builder builder)
    {
        this.marcSplitter = new MARCSplitter(builder.file);
        this.marcSplitter.setDebug(builder.debug);
        this.marcSplitter.setOutputOnModifiedOnly(builder.outputModifiedOnly);
        this.marcSplitter.setStrict(builder.isStrict);
        this.debug = builder.debug;
        this.marcSplitter.parseMARC();
    }

    /** Sets the instructions used to modify or test the marc file.
     * 
     * @param instructionList List of instructions, either test or modify.
     */
    public void runInstructions(List<Instruction> instructionList) 
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
        System.out.printf("Records printed %6d\n", Instruction.getPrintedRecords());
        System.out.printf("Records written %6d\n", Instruction.getWrittenRecords());
    }
    
    /**
    * Split the marc file. The splitter needs to take the stream find the leader,
    * the directory and data segments.
    * @author anisbet
    */
    class MARCSplitter
    {
        private final List<Record> marcRecords;
        private boolean isOutputIfModifiedOnly = false;
        private boolean debug = false;
        private boolean isStrict = false;
        private final String fileName;

        /**
         * Create a record object that can tell if it has been modified.
         * @param fileName name of the MARC file to read.
         * @param modify sets if the object returned is a Record or DirtyRecord.
         */
        MARCSplitter(String fileName)
        {
            marcRecords = new ArrayList<>();
            this.fileName = fileName;
        }

        /**
         * Standard setup for reading records.
         */
        public void parseMARC()
        {
            File f = new File(this.fileName);
            if (! f.isFile() )
            {
                throw new IllegalArgumentException(String.format("**Error: File '%s' doesn't exist.", this.fileName));
            }

            if (f.length() == 0)
            {
                throw new IllegalArgumentException(String.format("**Error: File '%s' has zero size.", this.fileName));
            }

            try
            {
                try (DataInputStream in = new DataInputStream(new FileInputStream(f))) {
                    int multilingualRecord = 0;
                    while (true)
                    {
                        Record record = readMARCRecord(in);
                        if (record == null)
                        {
                            break;
                        }
                        if (record.containsMultilingualCharacters())
                        {
                            multilingualRecord++;
                        }
                        marcRecords.add(record);
                    }
                    String fString = String.format("%10s %7d\n%10s %7d",
                            "multilingual records:", multilingualRecord, "total:", this.marcRecords.size());
                    System.out.println(fString);
                }
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

        private Record readMARCRecord(DataInputStream stream) throws IOException   
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
            Record.setStrict(isStrict);
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
            return marcRecords;
        }

        private void setStrict(boolean b) 
        {
            this.isStrict = b;
        }

        private void setOutputOnModifiedOnly(boolean b) 
        {
            this.isOutputIfModifiedOnly = b;
        }

        private void setDebug(boolean b) 
        {
            this.debug = b;
        }
    }
}
