
package MARC;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * MARC record data. This class manages coordination of data assembly based on
 * directory entries.
 * Directory of tags in a record.
 * CHARACTER POSITIONS
 * 
 * 00-02 - Tag
 * Three ASCII numeric or ASCII alphabetic characters (upper case or lower case, 
 * but not both) that identify an associated variable field.
 * 
 * 03-06 - Field length
 * Four ASCII numeric characters that specify the length of the variable field, 
 * including indicators, subfield codes, data, and the field terminator. A Field 
 * length number of less than four digits is right justified and unused positions 
 * contain zeros.
 * 
 * 07-11 - Starting character position
 * Five ASCII numeric characters that specify the starting character position of 
 * the variable field relative to the Base address of data (Leader/12-16) of the 
 * record. A Starting character position number of less than five digits is right 
 * justified and unused positions contain zeros.
 *  EXAMPLE
 *  	        Tag Field length Starting character position
 * Entry 1	001	0013	00000
 * Entry 2	008	0041	00013
 * Entry 3	050	0011	00054
 * 
 * @author Andrew Nisbet
 */
public class Record
{
    /**
     * Set strict/non-strict, allowing reporting but continues processing, verses
     * throwing an exception on encountering an error.
     * @param strict true if you wish throw exception on MARC error, false to ignore
     * and continue on.
     */
    public static void setStrict(boolean strict)
    {
        isRelaxedChecking = !strict;
        Tag.setStrict(strict);
    }
    
    /**
     * Returns a list of index integers where record separators are found.
     * @param recordArray the entire record as an array of bytes.
     * @return List of integers representing each character of the record.
     */
    private static List<Integer> splitOnRS(byte[] recordArray)
    {
        List<Integer> splits = new ArrayList<>();
        int counter = 0;
        for (byte b: recordArray)
        {
            if (b == Record.RS) 
            {
                splits.add(counter);
            }
            counter++;
        }
        return splits;
    }

    /**
     * Returns the tags of the MARC record.
     * @param endPosition position of the end of a directory entry, on a 12 byte boundary.
     * @param recordArray The complete array of bytes in a record.
     * @return List of tags in the MARC file.
     */
    private static List<String> getTags(int endPosition, byte[] recordArray)
    {
        // traverse the recordArray until the endPostion, splitting every 12 bytes.
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < endPosition; i += DirectoryEntry.LENGTH)
        {
            // read the first 3 bytes as the name of the tag.
            String tag = Utility.getByteRange(recordArray, i, i + 3);
            tags.add(tag);
        }
        return tags;
    }
    
    private final Leader leader;
    public final static int GS = 0x1d;
    public final static int RS = 0x1e;
    public final static int US = 0x1f;
    // Ingnore tags are not checked for UTF8 compliance.
    private final List<Tag> ignoreTags;
    private final List<DirectoryEntry> directoryEntries;
    private static boolean isRelaxedChecking = false;
    
    /**
     * Creates a record from the argument bytes.
     * @param leader object. Contains the remaining record's geometry.
     * @param recordArray bytes read from the MARC file.
     */
    public Record(Leader leader, byte[] recordArray)
    {
        this.leader           = leader;
        this.directoryEntries = new ArrayList<>();
        List<Integer> splits  = Record.splitOnRS(recordArray);
        // the first one on the list is a list of all the tags lengths and offsets, 
        // they are all 12 bytes long. Consume the first one because the rest are
        // the offsets to the data itself.
        List<String> tags = Record.getTags(splits.get(0), recordArray);
        int lastPosition = splits.remove(0);
        // advance one byte to avoid copying the RS byte.
        lastPosition++;
        // Now the lists are the same length let's proceed.
        int tagIndex = 0;
        for (int currentEndPosition: splits)
        {
            byte[] tagContent = Arrays.copyOfRange(recordArray, lastPosition, currentEndPosition);
            lastPosition = currentEndPosition +1;
            // get the name of the tag.
            String tagName = tags.get(tagIndex++);
            // make a new directory entry
            DirectoryEntry de = null;
            try
            {
                de = new DirectoryEntry(tagName, tagContent);
            }
            catch (MARCError e)
            {
                System.err.printf("** offending record's TCN is '%s'\n", this.getTCN());
                // Back up and assume this is the new record start if relaxed mode is on.
                if (isRelaxedChecking)
                {
                    // Just keep going, we've warned them, but the user isn't fussy.
                    // The user will be missing two records, one that started ok and 
                    // the one after that that broke on the wrong RS.
                    continue;
                }
                System.exit(3);
            }
            this.directoryEntries.add(de);
        }
        this.ignoreTags= new ArrayList<>();
        // Don't check these tags for compliance, they don't take free-form characters.
        this.ignoreTags.add(new Tag("008"));
        this.ignoreTags.add(new Tag("035"));
    }
    
    /**
     * Tests if the record contain multilingual characters.
     * @return true if the file contains multilingual characters, and false
     * otherwise.
     */
    public boolean containsMultilingualCharacters()
    {
        // Traverse all the tags and check the data for out-of-range characters.
        OUTER: for (DirectoryEntry de: this.directoryEntries)
        {
            for (Tag tag: this.ignoreTags)
            {
                if (tag.compareTo(de.getTag()) == 0)
                {
                    continue OUTER;
                }
            }
            if (de.containsMultilingualEncoding())
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @return all the tags in the record. Intention is to use 
     * this iterate-able list to reference the tag content via Record.
     */
    public List<String> getTags()
    {
        List<String> tags = new ArrayList<>();
        for (DirectoryEntry d: this.directoryEntries)
        {
            tags.add(d.tag.toString());
        }
        return tags;
    }
    
    /**
     * Gets the data payload of the Record object.
     * @return byte array of all the data of the record.
     */
    private byte[] getDataPayload()
    {
        List<Byte> dataBytes = new ArrayList<>();
        for (DirectoryEntry d: this.directoryEntries)
        {
            for (byte b: d.content.getBytes())
            {
                dataBytes.add(b);
            }
            // add the directory record separator RS
            dataBytes.add((byte)RS);
        }
        // And record terminator
        dataBytes.add((byte)GS);
        byte[] data = new byte[dataBytes.size()];
        int index = 0;
        for (byte b: dataBytes)
        {
            data[index++] = b;
        }
        return data;
    }
    
    /**
     * Gets all the bytes of the directory for the MARC record.
     * @return array of bytes of the MARC record directory.
     */
    private byte[] getDirectoryBytes()
    {
        List<Byte> outArray = new ArrayList<>();
        int previousDataStartPosition = 0;
//        int deIndex = 0;
        for (DirectoryEntry d: this.directoryEntries)
        {
            // the offset into the directory array for this entry is
//            int pos = deIndex++ * DirectoryEntry.LENGTH;
            // add the tag |xxx|000000000
//            outArray.addAll(Utility.getBytes(d.tag));
            outArray.addAll(d.tag.getBytesList());
            // add the length of the data not including the RS byte.
            //  000|xxxx|00000
            String dataLength = Utility.getZeroPaddedInteger(d.getDataLength(), 4);
            outArray.addAll(Utility.getBytes(dataLength));
            // add the offset to the data including the RS byte.
            //  0000000|xxxxx
            String startPosition = Utility.getZeroPaddedInteger(previousDataStartPosition, 5);
            outArray.addAll(Utility.getBytes(startPosition));
//            // Advance the position to the end of the previoius data including the RS byte.
            previousDataStartPosition += d.getDataLength();
        }
        // add the directory terminator RS
        outArray.add((byte)RS);
        byte[] dirArray = new byte[outArray.size()];
        int index = 0;
        for (byte b: outArray)
        {
            dirArray[index++] = b;
        }
        return dirArray;
    }

    /**
     * Writes the record to the argument OutputStream.
     * @param out the output stream.
     * @throws java.io.IOException if there was a problem during writing the record.
     */
    public void write(OutputStream out) throws IOException
    {
        // We have to update the leader with the new length of the logical record,
        // but to do that we need the length of the directory + length of the
        // data section.
        byte[] dir = this.getDirectoryBytes();
        byte[] dataPayload = this.getDataPayload();
        // If the content of the record has changed the leader needs to be updated.
        this.leader.setLogicalRecordLength(Leader.LENGTH + dir.length + dataPayload.length);
        out.write(this.leader.getLeaderBytes());
        out.write(dir);
        out.write(dataPayload);
    }
    
    /**
     * Returns the content of the first named tag or a byte array with one empty byte.
     * @param tag string name.
     * @return List of byte[] one for each tag that was found in the record in
     * the order they were found in.
     */
    public List<byte[]> getTagContent(String tag)
    {
        List<byte[]> returnArray = new ArrayList<>();
        for (DirectoryEntry d: this.directoryEntries)
        {
            if (d.getTag().isTag(tag))
            {
                returnArray.add(d.content.getBytes());
            }
        }
        return returnArray;
    }
    
    /**
     * Sets the content of a tag to the value supplied in the argument newContent.
     * @param tag name of the tag. Only operates on the first instance.
     * @param newContent - the new bytes to write as content for this DirectoryEntry.
     * @return true if the tag was found and data copied and false otherwise.
     */
    public boolean setTagContent(String tag, List<byte[]> newContent)
    {
        boolean result = false;
        for (DirectoryEntry d: this.directoryEntries)
        {
            if (d.getTag().isTag(tag))
            {
                byte[] newArray = newContent.remove(0);
                d.content = new Content(newArray);
                result = true;
            }
        }
        return result;
    }

    /**
     * 
     * @param tag the name of the desired tag as a String.
     * @return the string of the first tag encountered in the MARC record
     * or an empty string if the tag was not found.
     */
    public String getTag(String tag)
    {
        for (DirectoryEntry d: this.directoryEntries)
        {
            if (d.getTag().isTag(tag))
            {
                return d.content.toString();
            }
        }
        return "";
    }
    
    /**
     * Gets the contents of all the tags matching the argument tag.
     * @param tag name.
     * @return List of all the tags that match the argument tag from the record. 
     * If the record doesn't match any tag the list will be empty.
     */
    public List<DirectoryEntry> getTags(String tag)
    {
        List<DirectoryEntry> tagsContent = new ArrayList<>();
        for (DirectoryEntry de: this.directoryEntries)
        {
            if (de.getTag().isTag(tag))
            {
                tagsContent.add(de);
            }
        }
        return tagsContent;
    }

    /**
     * 
     * @return the contents of the '035' tag, or an empty string if the tag 
     * wasn't found in this record.
     */
    public final String getTCN()
    {
        // Final because called in constructor.
        // Re-factored.
        String tag = this.getTag("035");
        // clean the tag of any (Sirsi) and what ever.
        int pos = tag.lastIndexOf(" ");
        if (pos > -1)
        {
            // advance past the indicators '$' and 'a'
            return tag.substring(pos +3);
        }
        return tag;
    }
    
    /**
     * Sets the flag that indicates whether this is a UTF-8 or MARC8 record. 
     */
    public void setUTF8Flag()
    {
        this.leader.setUTF8EncodingFlag();
    }
    
    /**
     * 
     * @param search the language you are looking for.
     * @return string of the language encoding for this record.
     */
    public boolean matchesLanguageIndicator(String search)
    {
        // Refactored.
        // The 008 tag has language encoded like so:
        // 35-37 - Language
        // ### - No information provided
        // zxx - No linguistic content
        // mul - Multiple languages
        // sgn - Sign languages
        // und - Undetermined
        // [aaa] - Three-character alphabetic code
        String t008 = this.getTag("008");
        String language = t008.substring(35, 38);
//        System.err.printf("'%s'>>>>%s<<<<<\n", t008, language);
        return language.compareTo(search) == 0;
    }
    
    /**
     * Tests if the record's 9th position contains the marker for Unicode encoding. 
     * @return true if the 9th position is 'a' and false otherwise (MARC 8).
     */
    public boolean isUTF8()
    {
        return this.leader.isUTF8Encoding();
    }
    
    /**
     * Sets the encoding flag on the leader to MARC8.
     */
    public void setMARC8Flag()
    {
        this.leader.setMARC8EncodingFlag();
    }
    
    /**
     * 
     * @return Leader object of the MARC record.
     */
    public final Leader getLeader()
    {
        return this.leader;
    }
    
    /**
     * Removes a field from a record. If there is more than one field all fields
     * will be removed. 
     * @param tag name of the field.
     * @param match match this string in the field before considering deletion.
     * @return true if the field was found in the record and removed, and 
     * false otherwise.
     */
    public boolean removeTags(String tag, String match) 
    {
        // just remove all tag references and return.
        List<DirectoryEntry> myTargetDEntry = new ArrayList<>();
        for (DirectoryEntry dEntry: this.directoryEntries)
        {
            if (dEntry.getTag().isTag(tag))
            {
                myTargetDEntry.add(dEntry);
            }
        }
        if (myTargetDEntry.size() > 0)
        {
            for (DirectoryEntry dEntry: myTargetDEntry)
            {
                // match not specified, or match matched.
                if (match == null || match.isEmpty() || dEntry.contains(match))
                {
                    this.directoryEntries.remove(dEntry);
                }
            }
        }
        return myTargetDEntry.size() > 0;
    }
    
    /**
     * Removes a field from a record. If there is more than one field all fields
     * will be removed. 
     * @param tag name of the field.
     * @return true if the field was found in the record and removed, and 
     * false otherwise.
     */
    public boolean removeTags(String tag) 
    {
        // just remove all tag references and return.
        List<DirectoryEntry> myTargetDEntry = new ArrayList<>();
        for (DirectoryEntry dEntry: this.directoryEntries)
        {
            if (dEntry.getTag().isTag(tag))
            {
                myTargetDEntry.add(dEntry);
            }
        }
        if (myTargetDEntry.size() > 0)
        {
            for (DirectoryEntry dEntry: myTargetDEntry)
            {
                this.directoryEntries.remove(dEntry);
            }
        }
        return myTargetDEntry.size() > 0;
    }
    
    /**
     * Adds a field to a record. Use '$' as a field separator, so 'this$vthat'
     * creates the field with a subfield 'v' of 'that'.
     * @param tag String tag name. Can only be numeric.
     * @param tagContent string of the fields payload.
     * @return true if the field was not empty, not null, and created, and false otherwise.
     */
    public boolean addTag(String tag, String tagContent)
    {
        DirectoryEntry de = new DirectoryEntry(tag, tagContent);
        boolean result = this.directoryEntries.add(de);
        orderFields();
        return result;
    }
    
    /**
     * Orders the tag fields, numerically by tag.
     */
    public void orderFields()
    {
        Collections.sort(directoryEntries, new Comparator<DirectoryEntry>() 
        {
            public int compare(DirectoryEntry o1, DirectoryEntry o2) 
            {
                int diff = o1.compareTo(o2);
                if (diff > 0)
                   return 1;
                else if (diff <0)
                   return -1;
                else
                   return 0;
            }
        });
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        // output the leader.
        sb.append(this.leader.toString());
        sb.append("\n");
        for (DirectoryEntry dEntry: this.directoryEntries)
        {
            sb.append(dEntry.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
