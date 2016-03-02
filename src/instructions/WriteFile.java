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

import MARC.DirtyRecord;
import MARC.Record;
import instructions.Parser.SyntaxError;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instruction in the form of "record[s] write [name] as [text|binary]".
 * 
 * @author Andrew
 */
class WriteFile extends Instruction 
{

    private static String fileName; // TODO add a sequence number to the file name to make it unique.
    private boolean outputBinary;
    private final List<Record> records;
    private boolean runNow;

    public WriteFile(List<String> tokens) throws SyntaxError 
    {
        this.outputBinary   = true; // Default to output binary MARC file.
        this.runNow         = false;
        this.records        = new ArrayList<>();
        this.tag            = tokens.remove(0);
        this.verb           = tokens.remove(0);
        WriteFile.fileName  = tokens.remove(0);
        String assign       = tokens.remove(0);
        String format       = tokens.remove(0);
        switch (this.tag)
        {
            case "record":
                this.runNow = true; // runs for each record.
                break;
            case "records":
                this.runNow = false; // runs only when have finished with all records.
                break;
            default:
                throw new UnsupportedOperationException(String.format("operation '%s' not supported.", this.tag));
        }
        
        switch (this.verb)
        {
            case "write":
                break;
            default:
                throw new SyntaxError(String.format("operation '%s' not supported.", this.verb));
        }
        // user can specify 'as text' or 'as binary'.
        switch (assign)
        {
            case "as":
                break;
            default:
                throw new SyntaxError(String.format("assignment operator '%s' not supported.", assign));
        }
        
        switch (format)
        {
            case "text":
                this.outputBinary = false;
                break;
            case "binary":
                this.outputBinary = true;
                break;
            default:
                throw new UnsupportedOperationException(String.format("output format '%s' not supported.", format));
        }
    }

    @Override
    public void setRecord(Record record) 
    {
        this.records.add(record);
    }
    
    /**
     * Writes the marc record as a standard marc binary file.
     * @return true if the file was created correctly and false otherwise.
     */
    private boolean runBinaryOutput()
    {
        FileOutputStream out = null;
        try 
        {
            out = new FileOutputStream(this.fileName);
            for (Record r: this.records)
            {
                // there may be no changes to the record, but we want the record to
                // be output if the user has selected so in the intructions.
                if (Environment.isOutputOnChangeOnly() && r instanceof DirtyRecord)
                {
                    // If no changes and doesn't match on say a filter selection skip it.
                    if (((DirtyRecord)r).isDirty() == false)
                    {
                        continue;
                    }
                }
                r.write(out);
                Environment.incrementWrittenRecords();
            }
            out.flush();
            out.close();
            this.records.clear();
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(WriteFile.class.getName()).log(Level.SEVERE, 
                    "** error file should have been created!" , ex);
            return false;
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(WriteFile.class.getName()).log(Level.SEVERE, 
                    "** error writing file " + this.fileName, ex);
            return false;
        } 
        finally 
        {
            try 
            {
                out.close();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(WriteFile.class.getName()).log(Level.SEVERE, 
                        "** error closing the file " + this.fileName, ex);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Writes a the marc record out as text.
     * @return true if the file was created correctly and false otherwise.
     */
    private boolean runTextOutput()
    {
        try (PrintWriter writer = new PrintWriter(this.fileName, "UTF-8")) 
        {
            for (Record r: this.records)
            {
                // there may be no changes to the record, but we want the record to
                // be output if the user has selected so in the intructions.
                if (Environment.isOutputOnChangeOnly() && r instanceof DirtyRecord)
                {
                    // If no changes and doesn't match on say a filter selection skip it.
                    if (((DirtyRecord)r).isDirty() == false)
                    {
                        continue;
                    }
                }
                writer.println(r.toString());
                Environment.incrementWrittenRecords();
            }
            writer.flush();
            writer.close();
            // Clear the records so every time we are requested to ouput a file
            // we only output the records we haven't handled yet.
            this.records.clear();
        } 
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(WriteFile.class.getName()).log(Level.SEVERE, 
                    "** error, file should have been created!", ex);
            return false;
        }
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(WriteFile.class.getName()).log(Level.SEVERE, 
                    "** error, UTF-8 not supported.", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean run() 
    {
        if (this.runNow)
        {
            if (this.outputBinary)
            {
                return runBinaryOutput();
            }
            return runTextOutput();
        }
        // else just return true until the actual work get done in finalize.
        return true;
    }
    
    @Override
    public boolean setFinalize()
    {
        this.runNow = true;
        // Don't run if there aren't any records, it will zero out the file.
        if (this.records.size() > 0)
        {
            return run();
        }
        return true;
    }
}
