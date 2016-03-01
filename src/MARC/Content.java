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
    // Indicators -- The use of indicators is explained in fields where 
    // they are used. Indicators are one-digit numbers. Beginning with 
    // the 010 field, in every field -- following the tag -- are two 
    // character positions, one for Indicator 1 and one for Indicator 2. 
    // The indicators are not actually defined in all fields, however. 
    // And it is possible that a 2nd indicator will be used, while the 1st 
    // indicator remains undefined (or vice versa). When an indicator is 
    // undefined, the character position will be represented by the 
    // character # (for blank space).
    public final static int START_INDICATOR   = 10; // threshold after which all tags have indicators.
    public final static int GS                = 0x1d;
    public final static int RS                = 0x1e;
    public final static int US                = 0x1f;
    private String content;
    
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
        byte[] arr = new byte[bytes.length];
        int i = 0;
        for (byte b: bytes)
        {
            if (b == RS) 
            {
                continue;
            }
            arr[i++] = b;
        }
        this.content = new String();
        try
        {
            this.content = new String(arr, "UTF-8").replace((char)US, '$');
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Content.class.getName()).log(Level.SEVERE, 
                    "Unsupported conversion 'UTF-8'.", ex);
        }
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
     * Produces the content in the same format as Marcedit.
     * @param tagName name of the tag this content refers to.
     * @return String version of the content in mrk style.
     */
    public String getMRKString(String tagName)
    {
        // convert to a number. Tags below 10 have all spaces replaced with '\'
        int iTag = 0;
        try
        {
            iTag = Integer.parseInt(tagName.trim());
        }
        catch (NumberFormatException e)
        {
            // This ocurs if the LDR is passed.
            if (! tagName.equalsIgnoreCase(Leader.TAG))
            {
                System.err.printf("** error expected tag but got '%s'\n", tagName);
            }
            System.exit(1);
        }
        if (iTag < START_INDICATOR)
        {
            return this.content.replace((char)0x20, '\\');
        }
        StringBuilder sb = new StringBuilder();
        if (this.content.charAt(0) == 0x20) 
        {
            sb.append('\\');
        }
        else
        {
            sb.append(this.content.charAt(0));
        }
        if (this.content.charAt(1) == 0x20) 
        {
            sb.append('\\');
        }
        else
        {
            sb.append(this.content.charAt(1));
        }
        sb.append(this.content.substring(2));
        // now append the substring of chars after the indicators
        return sb.toString();
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
