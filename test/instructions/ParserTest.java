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
package instructions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew
 */
public class ParserTest 
{


    /**
     * Test of readQuotedTokens method, of class Parser.
     */
    @Test
    public void testReadQuotedTokens() {
        System.out.println("==readQuotedTokens==");
        String command = "008 cook that";
        Parser instance = new Parser(true);
        List<String> result = Parser.readQuotedTokens(command);
        for (String s: result)
        {
            System.out.printf("1>%s<<\n", s);
        }
        
        result = Parser.readQuotedTokens("hello \"there\" you");
        for (String s: result)
        {
            System.out.printf("2>%s<<\n", s);
        }
        result = Parser.readQuotedTokens("hello \"there\"");
        for (String s: result)
        {
            System.out.printf("3>%s<<\n", s);
        }
        result = Parser.readQuotedTokens("\"hello\" there");
        for (String s: result)
        {
            System.out.printf("4>%s<<\n", s);
        }
        result = Parser.readQuotedTokens("   \"hello\" there here is another String;");
        for (String s: result)
        {
            System.out.printf("5>%s<<\n", s);
        }
        result = Parser.readQuotedTokens("   hello\" there here is another String;");
        for (String s: result)
        {
            System.out.printf("6>%s<<\n", s);
        }
    }
    
}
