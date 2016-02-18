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

import java.util.List;

/**
 * Class signals that it has had changes applied to it.
 * @author anisbet
 */
public class DirtyRecord extends MARC.Record
{
    private boolean dirtyBit;

    /** 
     * Constructor.
     * @param leader Leader object.
     * @param recordArray bytes of the record.
     */
    public DirtyRecord(Leader leader, byte[] recordArray)
    {
        super(leader, recordArray);
        this.dirtyBit = false;
    }
    
    @Override
    public void setUTF8Flag()
    {
        this.dirtyBit = true;
        super.setUTF8Flag();
    }
    
    @Override
    public void setMARC8Flag()
    {
        this.dirtyBit = true;
        super.setMARC8Flag();
    }
    
    @Override
    public boolean removeTags(String tag, String match)
    {
        boolean result = super.removeTags(tag, match);
        this.dirtyBit = true;
        return result;
    }
    
    @Override
    public boolean removeTags(String tag)
    {
        boolean result = super.removeTags(tag);
        this.dirtyBit = true;
        return result;
    }
    
    @Override
    public boolean addTag(String tag, String tagContent)
    {
        boolean result = super.addTag(tag, tagContent);
        this.dirtyBit = true;
        return result;
    }
    
    @Override
    public void orderFields()
    {
        super.orderFields();
        this.dirtyBit = true;
    }
    
    @Override
    public boolean setTagContent(String tag, List<byte[]> newContent)
    {
        boolean result = super.setTagContent(tag, newContent);
        this.dirtyBit = true;
        return result;
    }
    
    /**
     * Test if modifications have been made to the record.
     * @return true if the record has been modified and false otherwise.
     */
    public boolean isDirty()
    {
        return this.dirtyBit;
    }
    
    /**
     * Allows the record to be touched even if there are no changes.
     */
    public void touch()
    {
        this.dirtyBit = true;
    }
}
