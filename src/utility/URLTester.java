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
package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Gets the content of a web page and can test if a phrase is contained in the page.
 * @author anisbet
 */
public class URLTester
{
    private String testURL = new String();
    private StringBuilder content = new StringBuilder();
	
    public URLTester(String linkTarget)
    {
        this.testURL = linkTarget;
        URL url = null;
        try 
        {
            url = new URL( this.testURL );

            BufferedReader in;
            in = new BufferedReader( new InputStreamReader( url.openStream() ) );
            String inputLine;

            while ( ( inputLine = in.readLine() ) != null )
            {
                this.content.append(inputLine);
            }

            in.close();
        } 
        catch (IOException e) 
        {
            if (url == null)
            {
                System.err.printf("** error the argument URL is null\n");
            }
            else
            {
                System.err.printf("** error reading URL '%s'\n", this.testURL);
            }
        }
    }

    /**
     * This is silly and clumsy but it works.
     * @param testPhrase phrase to search for in the content like "404 not found" etc.
     * @return true if the argument phrase was found in the content and false otherwise.
     */
    public boolean search(String testPhrase)
    {
        return this.content.toString().contains(testPhrase);
    }

    /**
     * @return Content of the web page as a single string.
     */
    public String getContent() 
    {
        return this.content.toString();
    }
}
