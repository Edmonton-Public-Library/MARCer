/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MARC;

import MARC.Record;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anisbet
 */
public class ResultFileWriter 
{
    // http://stackoverflow.com/questions/2885173/java-how-to-create-a-file-and-write-to-a-file
    public static void writeToTextFile(String name, List<String> list)
    {
        try (PrintWriter writer = new PrintWriter(name, "UTF-8")) 
        {
            for (String result: list)
            {
                writer.println(result);
            }
            writer.close();
        } 
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(ResultFileWriter.class.getName()).log(Level.SEVERE, 
                    " File should have been created!", ex);
        }
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(ResultFileWriter.class.getName()).log(Level.SEVERE, 
                    "UTF-8 not supported.", ex);
        }
    }
    
    public static void writeBytesFile(String name, Record record)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(name);
            record.write(out);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResultFileWriter.class.getName()).log(Level.SEVERE, 
                    " File should have been created!" , ex);
        } catch (IOException ex) {
            Logger.getLogger(ResultFileWriter.class.getName()).log(Level.SEVERE, 
                    " Error writing file " + name, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(ResultFileWriter.class.getName()).log(Level.SEVERE, 
                        "error closing the file " + name, ex);
            }
        }
    }
}
