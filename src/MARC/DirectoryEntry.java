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

/*
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
 * @author Andrew Nisbet <anisbet@epl.ca>
 */
public class DirectoryEntry implements Comparable 
{
    public static final int LENGTH = 12;
    protected Tag tag;
    protected Content content;

    public DirectoryEntry(String name, String data) 
    {
        this.tag = new Tag(name);
        this.content = new Content(data);
    }
    
    public DirectoryEntry(String name, byte[] data) 
    {
        this.tag = new Tag(name);
        this.content = new Content(data);
    }

    @Override
    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
//        sb.append(this.tag).append("<=>").append(Utility.getString(content));
        sb.append(this.tag).append("<=>").append(this.content);
        return sb.toString();
    }

    public Tag getTag() 
    {
        return this.tag;
    }

    public boolean containsMultilingualEncoding() 
    {
//        for (byte b : this.content) {
        for (byte b : this.content.getBytes()) {
            // from the specification: http://www.loc.gov/marc/specifications/specchargeneral.html
            //            if (b < 0xffffff7f)
            if (b < 0) 
            {
                if (b != Content.GS && b != Content.RS && b != Content.US) 
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return length of the data content of the tag including the RS.
     */
    public int getDataLength() 
    {
//        return this.content.length + 1;
        return this.content.getSize() + 1;
    }

    @Override
    public int compareTo(Object o) 
    {
        DirectoryEntry de = (DirectoryEntry) o;
        return this.tag.compareTo(de.tag);
    }

    /**
     * Checks if the contents of the tag matches the argument string.
     * @param match the string to match to. Doesn't have to be exact or complete match.
     * @return true if the field matches all or part of the field's content and false
     * otherwise.
     */
    public boolean contains(String match) 
    {
        return this.content.contains(match); 
    }
/**
 * Gets the content from the directory entry.
 * @return the contents of the DirectoryEntry.
 */
    public Content getContent() 
    {
        return this.content;
    }
    
}
