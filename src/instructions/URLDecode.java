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
package instructions;

import MARC.DirectoryEntry;
import MARC.DirtyRecord;
import MARC.Record;
import instructions.Parser.SyntaxError;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Decodes a URL in a specific subfield of a given tag.
 * Syntax: 856 url_decode u
 * All URLs are expected to appear within a subfield in a tag.
 * @author Andrew Nisbet.
 */
public class URLDecode extends Instruction
{
    private Record record;
    private char subfield;
    
    public URLDecode(List<String> tokens) 
            throws SyntaxError
    {
        this.tag  = tokens.remove(0);
        this.verb = tokens.remove(0);
//        Syntax: 856 decode subfield u
        String token = tokens.remove(0);
        if (token.length() != 1)
        {
            throw new SyntaxError(String.format("** error invalid subfield '%s'\n", token));
        }
        this.subfield = token.charAt(0); 
    }

    @Override
    public void setRecord(Record record)
    {
        this.record = record;
    }

    @Override
    public boolean run()
    {
        List<DirectoryEntry> urls = this.record.getTags(this.tag);
        for (DirectoryEntry de: urls)
        {
            String url = de.getContent().extractSubfield(this.subfield);
            // Somtimes we get a url redirect. Java's decode doesn't handle that so break it appart.
            String urlDecoded = decodeCompositeURL(url);
            // This is a little backwards, URL are expected to be valid if they
            // don't find the predicate in the page's content.
            if (! url.isEmpty() && ! urlDecoded.isEmpty())
            {
                de.getContent().replaceSubField(urlDecoded, this.subfield);
                if (Environment.isOutputOnChangeOnly() && this.record instanceof DirtyRecord)
                {
                    // If no changes and doesn't match on say a filter selection skip it.
                    ((DirtyRecord)this.record).touch(); 
                }
                return true;
            }
        }
        System.err.printf("** URL test failed on TCN '%s'\n", this.record.getTCN());
        return false;
    }
    
    private String decodeCompositeURL(String fullURL)
    {
        String[] redirects = fullURL.split("http");
        StringBuilder sb = new StringBuilder();
        try
        {
            for (String urlPortion: redirects)
            {
                if (urlPortion.isEmpty()) continue;
                String url = "http" + urlPortion;
                sb.append(java.net.URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
            }
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(URLDecode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    
}
