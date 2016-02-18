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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew
 */
public class ContentTest 
{
    private final byte[] byteContent;
    
    public ContentTest()
    {
        this.byteContent = new byte[32];
        this.byteContent[0] = ' ';
        this.byteContent[1] = '#';
        this.byteContent[2] = 0x1f;
        for (int i = 3; i < this.byteContent.length; i++)
        {
            this.byteContent[i] = (byte)'c';
        }
    }

    /**
     * Test of getRange method, of class Content.
     */
    @Test
    public void testGetByteRange() 
    {
        System.out.println("==getByteRange==");
        int start = 3;
        int end = 6;
        Content instance = new Content(this.byteContent);
        String expResult = "ccc";
        String result = instance.getRange(start, end);
        assertEquals(expResult, result);
        // Oversize test.
        expResult = "ccc";
        result = instance.getRange(28, 31);
        assertEquals(expResult, result);
        result = instance.getRange(28, 70);
        expResult = "cccc";
        assertEquals(expResult, result);
        expResult = " #$ccccccccccccccccccccccccccccc";
        System.out.println(expResult);
        System.out.println(instance.getRange(0, 32));
        assertEquals(expResult, instance.getRange(0, 32));
    }

    /**
     * Test of getBytes method, of class Content.
     */
    @Test
    public void testGetBytes() 
    {
        System.out.println("==getBytes==");
        
        byte[] bContent = new byte[32];
        bContent[0] = ' ';
        bContent[1] = '#';
        bContent[2] = 0x1f;
        for (int i = 3; i < bContent.length; i++)
        {
            bContent[i] = (byte)'c';
        }
        Content instance = new Content(bContent);
        byte[] result = instance.getBytes();
        assertArrayEquals(this.byteContent, result);
    }

    /**
     * Test of getBytesList method, of class Content.
     */
    @Test
    public void testGetBytesList() 
    {
        System.out.println("==getBytesList==");
        byte[] bContent = new byte[11];
        bContent[0] = ' ';
        bContent[1] = '#';
        bContent[2] = 0x1f;
        bContent[3] = 'o';
        bContent[4] = 'n';
        bContent[5] = 'e';
        bContent[6] = 0x1f;
        bContent[7] = 'v';
        bContent[8] = 't';
        bContent[9] = 'w';
        bContent[10] = 'o';
        Content instance = new Content(bContent);
        assertTrue(instance.getSize() == 11);
        int i = 0;
        for (Byte b: instance.getBytesList())
        {
            System.out.printf("%s, ", b.toString());
            assertTrue(b.byteValue() == bContent[i++]);
        }
    }

    /**
     * Test of toString method, of class Content.
     */
    @Test
    public void testToString() 
    {
        System.out.println("==toString==");
        Content instance = new Content(this.byteContent);
        assertTrue(" #$ccccccccccccccccccccccccccccc".compareTo(instance.toString()) == 0);
    }

    /**
     * Test of testIndicator method, of class Content.
     */
    @Test
    public void testTestIndicator()
    {
        System.out.println("==testIndicator==");
        String whichIndicator = "1";
        
        Content instance = new Content(this.byteContent);
        assertTrue(instance.testIndicator("2", "#"));
        assertTrue(instance.testIndicator("1", " "));
        assertFalse(instance.testIndicator("1", "B"));
        assertFalse(instance.testIndicator("2", "\t"));
    }

    /**
     * Test of getFirstIndicator method, of class Content.
     */
    @Test
    public void testGetFirstIndicator()
    {
        System.out.println("==getFirstIndicator==");
        Content instance = new Content(" #");
        char expResult = ' ';
        char result = instance.getFirstIndicator();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSecondIndicator method, of class Content.
     */
    @Test
    public void testGetSecondIndicator()
    {
        System.out.println("==getSecondIndicator==");
        Content instance = new Content(" #");
        char expResult = '#';
        char result = instance.getSecondIndicator();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSize method, of class Content.
     */
    @Test
    public void testGetSize()
    {
        System.out.println("==getSize==");
        Content instance = new Content();
        int expResult = 0;
        int result = instance.getSize();
        assertEquals(expResult, result);
        instance = new Content(this.byteContent);
        assertEquals(32, instance.getSize());
    }

    /**
     * Test of contains method, of class Content.
     */
    @Test
    public void testContains()
    {
        System.out.println("==contains==");
        String match = "ccc";
        Content instance = new Content(this.byteContent);
        boolean result = instance.contains(match);
        assertTrue(result);
    }

    /**
     * Test of subfieldContains method, of class Content.
     */
    @Test
    public void testSubfieldContains()
    {
        System.out.println("==subfieldContains==");
        String match = "two";
        byte[] bContent = new byte[11];
        bContent[0] = ' ';
        bContent[1] = '#';
        bContent[2] = 0x1f;
        bContent[3] = 'o';
        bContent[4] = 'n';
        bContent[5] = 'e';
        bContent[6] = 0x1f;
        bContent[7] = 'v';
        bContent[8] = 't';
        bContent[9] = 'w';
        bContent[10] = 'o';
        Content instance = new Content(bContent);
        
        assertTrue(instance.subfieldContains("two", 'v'));
        assertFalse(instance.subfieldContains("two", 'p'));
    }
    
}
