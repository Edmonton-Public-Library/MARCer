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

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew
 */
public class TagTest {
    
    public TagTest() {
    }

    /**
     * Test of getBytesList method, of class Tag.
     */
    @Test
    public void testGetBytesList() 
    {
        System.out.println("== getBytesList ==");
        Tag instance = new Tag("008");
        for (Byte b: instance.getBytesList())
        {
            System.out.printf("B::%s\n", b.toString());
        }
    }

    /**
     * Test of toString method, of class Tag.
     */
    @Test
    public void testToString() 
    {
        System.out.println("==toString==");
        Tag instance = new Tag("856");
        assertTrue(instance.toString().compareTo("856") == 0);
    }

    /**
     * Test of isTag method, of class Tag.
     */
    @Test
    public void testIsTag() 
    {
        System.out.println("==isTag==");
        Tag instance = new Tag("856");
        assertTrue(instance.isTag("856"));
        assertFalse(instance.isTag("009"));
    }

    /**
     * Test of compareTo method, of class Tag.
     */
    @Test
    public void testCompareTo() 
    {
        System.out.println("==compareTo==");
        Tag instance = new Tag("856");
        Tag otherInstance = new Tag("856");
        assertTrue(instance.compareTo(otherInstance) == 0);
        otherInstance = new Tag("900");
        assertFalse(instance.compareTo(otherInstance) == 0);
    }
    
}
