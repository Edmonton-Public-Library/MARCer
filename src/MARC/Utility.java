/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MARC;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class is not intended to be instantiated in and of itself.
 * @author Andrew Nisbet
 */
public class Utility
{

    /**
     * Converts an array of bytes into a string, replaces US with '$' for visibility.
     * @param bytes array of bytes that is to be converted into a String.
     * @return string version of the argument byte array.
     */
    public static String getString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for(byte b: bytes)
        {
            if (b == Record.US)
            {
                sb.append("$");
            }
            else
            {
                sb.append((char)b);
            }
        }
        return sb.toString().trim(); // trim off the RS and US chars.
    }
    
    public static List<Byte> getBytes(String s)
    {
        List<Byte> array = new ArrayList<>();
        for (String str: s.split("|"))
        {
            if (str.isEmpty())
            {
                continue;
            }
            array.add((byte)str.charAt(0));
        }
        return array;
    }
    
    private Utility(){}
    
    /**
     * Copies the contents of the DirectoryEntry into the byte[] out, starting 
     * at position 'position'.
     * @param dst byte buffer where the data will be written.
     * @param position start position index of out array where data will be written.
     * @param src data source.
     */
    public static void copy(byte[] dst, int position, byte[] src)
    {
        int indexCount = position;
        for (byte b: src)
        {
            dst[indexCount] = b;
            indexCount++;
        }
    }
    
    /**
     * Copies the contents of the DirectoryEntry into the byte[] out, starting 
     * at position 'position'.
     * @param dst byte buffer where the data will be written.
     * @param position start position index of out array where data will be written.
     * @param src data source.
     */
    public static void copy(byte[] dst, int position, String src)
    {
        if (dst.length < position + src.length())
        {
            throw new IllegalArgumentException("** error, dst array is too small "
                    + "for src string insert at offset " + position);
        }
        int indexCount = position;
        String[] ss = src.split("|");
        for (String s: ss)
        {
            if (s.isEmpty() == false)
            {
                dst[indexCount] = (byte)s.charAt(0);
                indexCount++;
            }
        }
    }
    
    /**
     * Like the string counterpart, this function trims off the RS, GS, and US
     * fields off of byte arrays. This is done to facilitate character positional
     * testing within a MARC tag that may be prefixed by these characters.
     * @param content array of byte data
     * @param cleanedContent array where clean data will be buffered. Must be same size 
     * as content[] or smaller.
     */
    public static void trim(final byte[] content, final byte[] cleanedContent)
    {
        String contentString = Utility.getString(content);
        byte[] bs = contentString.getBytes(Charset.forName("UTF-8"));
        int i = 0;
        for (byte b: bs)
        {
            cleanedContent[i++] = b;
        }
    }
    
    /**
     * Returns a string version of the argument integer 'n' left-padded 
     * with zeros.
     * @param n integer to be formatted.
     * @param width total number of characters in the returned value.
     * @return string of the integer argument 'n' left-padded with zeros.
     */
    public static String getZeroPaddedInteger(int n, int width)
    {
        String returnString;
        switch (width)
        {
            case 3:
                returnString = String.format("%03d", n);
                break;
            case 4:
                returnString = String.format("%04d", n);
                break;
            case 5:
                returnString = String.format("%05d", n);
                break;   
            default:
                returnString = String.valueOf(n);
                break;
        }
        return returnString;
    }
    
    public static String getByteRange(byte[] leader, int start, int end)
    {
        byte[] chars = Arrays.copyOfRange(leader, start, end);
        StringBuilder sb = new StringBuilder();
        for (byte b: chars)
        {
            sb.append((char)b);
        }
        return sb.toString();
    }
}
