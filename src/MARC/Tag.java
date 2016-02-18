/*
 * Copyright 2016 Andrew.
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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tag for a MARC field.
 * @author Andrew
 */
public class Tag implements Comparable
{
    private final String tag;
    
    /**
     * Create tag from given name. Checks that the tag is a valid tag name, that is
     * the tag is a string of 3 character numbers that range from 001 to 999.
     * @param name the tags name as a String (like '035').
     */
    public Tag(String name)
    {
        try
        {
            int tagNumber = Integer.parseInt(name);
            if (tagNumber < 1 || tagNumber > 999)
            {
                String msg = String.format("** error, invalid tag name\n."
                    + "Must be a value between '001' and '999' but got '%s'\n", name);
                throw new IllegalArgumentException(msg);
            }
        }
        catch (NumberFormatException e)
        {
            System.err.printf("** error, invalid tag name\n."
                    + "Must be a value between '001' and '999'.\n%s\n", name);
            System.exit(3);
        }
        this.tag = name;
    }
    
    /**
     * Computes the integer value of a given tag.
     * @param tagName the tag as a String.
     * @return integer value of a named tag.
     */
    public static int getIntTag(String tagName)
    {
        // Convert the tag name into an integer if possible.
        int tagNumber = Integer.parseInt(tagName);
        return tagNumber;
    }
    
    /**
     * Creates an list of all the bytes in the content.
     * @return List of all bytes.
     */
    public List<Byte> getBytesList()
    {
        List<Byte> array = new ArrayList<>();
        for (String tagString: this.tag.split("|"))
        {
            array.add((byte)tagString.charAt(0));
        }
        return array;
    }
    
    @Override
    public String toString()
    {
        return this.tag;
    }
    
    /**
     * Tests a given tag object for match on name.
     * @param tag Name of searched-for tag.
     * @return true if this object has the argument name and false otherwise.
     */
    public boolean isTag(String tag)
    {
        return this.tag.compareTo(tag) == 0;
    }

    @Override
    public int compareTo(Object o) 
    {
        // For sorting.
        Tag testTag = (Tag)o;
        return this.tag.compareTo(testTag.tag);
    }
}
