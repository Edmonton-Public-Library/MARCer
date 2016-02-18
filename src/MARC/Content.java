/*
 * Copyright 2016 anisbet.
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
package MARC;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Content of a MARC 21 field.
 * @author anisbet
 */
public class Content 
{
    public final static int DEFAULT_SIZE      = 32;
    public final static char SUB_FIELD_CHAR   = '$';
    public final static int GS                = 0x1d;
    public final static int RS                = 0x1e;
    public final static int US                = 0x1f;
    private final String content;
    
    /**
     * Creates an empty content array of size 256 bytes.
     */
    public Content()
    {
        this.content = new String();
    }
    
    /**
     * Creates a content object from the argument bytes.
     * @param bytes the payload for this object as an array of bytes.
     */
    public Content(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for(byte b: bytes)
        {
            if (b == US)
            {
                sb.append(SUB_FIELD_CHAR);
            }
            else
            {
                sb.append((char)b);
            }
        }
        this.content = sb.toString();
    }
    
    /**
     * Reports if the tag has an indicator, at a given position and if that indicator
     * has the argument value.
     * @param whichIndicator which indicator, either 1 or 2 (not zero indexed).
     * @param value value the indicator should have to test true.
     * @return true if the indicator at the argument position matches the argument
     * value and false otherwise.
     * @throws IllegalArgumentException if the indicator is neither '1' or '2'
     */
    public boolean testIndicator(String whichIndicator, String value)
    {
        switch (whichIndicator)
        {
            case "1":
                return this.getFirstIndicator() == value.charAt(0);
            case "2":
                return this.getSecondIndicator() == value.charAt(0);
            default:
                throw new IllegalArgumentException(
                        String.format("** error, expected indicator 1 or 2 but got '%s'.\n", whichIndicator));
        }
    }
    
    /**
     * Returns indicator 1 from the content.
     * @return indicator char which can be a space.
     */
    public char getFirstIndicator()
    {
        return this.content.charAt(0);
    }
    
    /**
     * 
     * @return character at indicator position 2. Only reliable for tags greater
     * than equal to 010.
     */
    public char getSecondIndicator()
    {
        return this.content.charAt(1);
    }
    
    /**
     * Creates a Content object from the argument String.
     * Bytes are converted assuming they belong to the UTF-8 character set.
     * @param string the payload for this object as a String.
     */
    public Content(String string)
    {
        this.content = string;
    }
    
    /**
     * Returns the number of bytes the content currently occupies.
     * @return integer of the size of the content string.
     */
    public int getSize()
    {
        return this.content.length();
    }
    
    /**
     * Returns the index of the value defined by argument and -1 if the value could not be found.
     * @param which integer value of the character you are testing.
     * @return the index of the end of the directory and -1 if the character wasn't found.
     * was not found.
     */
    public int indexOf(int which)
    {
        return this.content.indexOf(which);
    }
    
    /**
     * Returns a substring of the content.
     * @param start Start of the byte range of interest.
     * @param end end of the byte range of interest.
     * @return the string value of the byte range which could be empty if you screw
     * up on start and end, and on how {@link String#substring(int, int)} works.
     */
    public String getRange(int start, int end)
    {
        if (this.content.isEmpty()) return "";
        int s; // start
        int e; // end
        if (start < 0) 
        {
            s = 0;
        }
        else
        {
            s = start;
        }
        if (end >= this.content.length()) 
        {
            e = this.content.length();
        }
        else
        {
            e = end;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = s; i < e; i++)
        {
            sb.append(this.content.charAt(i));
        }
        return sb.toString();
    }
    
    /**
     * Returns the byte array representation of the field content.
     * @return array of bytes.
     */
    public byte[] getBytes()
    {
        byte[] bytes = new byte[this.content.length()];
        // remove any subfield delimiter.
        String newString = this.content.replace(SUB_FIELD_CHAR, (char)Content.US);
        try 
        {
            bytes = newString.getBytes("UTF-8");
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(Content.class.getName()).log(Level.SEVERE, "** error, some chars could not be converted.\n", ex);
        }
        return bytes;
    }
    
    /**
     * Creates an list of all the bytes in the content.
     * @return List of all bytes.
     */
    public List<Byte> getBytesList()
    {
        List<Byte> array = new ArrayList<>();
        byte[] bArray = this.getBytes();
        for (byte b: bArray)
        {
            if (b == SUB_FIELD_CHAR)
            {
                array.add((byte)US);
            }
            else
            {
                array.add(b);
            }
        }
        return array;
    }
    
    /**
     * 
     * @return String version of the content with subfields delimited with {@link #SUB_FIELD_CHAR}.
     */
    @Override
    public String toString()
    {
        return this.content;
    }

    /**
     * Checks if the content of this field subfieldContains the likes of the argument.
     * @param match the string fragment to search on.
     * @return true if the field match occurs in the field.
     */
    boolean contains(String match) 
    {
        return this.content.contains(match);
    }
    
    /**
     * Checks if the field subfieldContains the string in a subfield.
     * @param match string match.
     * @param c Subfield letter.
     * @return true if the subfield subfieldContains the argument string and false otherwise.
     */
    boolean subfieldContains(String match, char c) 
    {
        StringBuilder sb = new StringBuilder();
        // Build up the regex.
        sb.append("\\").append(Content.SUB_FIELD_CHAR).append(c);
        String[] subFields = this.content.split(sb.toString());
        if (subFields.length < 2) return false;
        for (String sub: subFields)
        {
            if (sub.contains(match)) return true;
        }
        return false;
    }
}
