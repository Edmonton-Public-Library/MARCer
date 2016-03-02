/*
 * Copyright 2016 Andrew Nisbet.
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

import java.util.HashMap;

/**
 * Environment settings. The environment has some built-in variables;
 * <ul>
 * <li>debug</li>
 * <li>isStrict</li>
 * <li>outputOnChangeOnly</li>
 * <li>writtenRecords</li>
 * <li>printedRecords</li>
 * <li>marcRecordCount</li>
 * <li>commandFileLineNumber</li>
 * But other arbitrary variables can be stored (as Strings) with the {@link Variable}
 * object.
 * </ul>
 * @author Andrew
 */
public class Environment
{
    private static boolean isStrict = false;
    private static boolean debug = false;
    private static String marcFile = "";
    private static boolean outputOnChangeOnly = false;
    private static int writtenRecords = 0;
    private static int printedRecords = 0;
    private static int marcRecordCount = 0;
    private static int commandFileLineNumber = 0;
    private static HashMap<String, String> symbolTable = new HashMap<>();

    /**
     * @return the isStrict
     */
    public static boolean isStrict() 
    {
        return isStrict;
    }

    /**
     * @param aIsStrict the isStrict to set
     */
    public static void setStrict(boolean aIsStrict) 
    {
        isStrict = aIsStrict;
    }

    /**
     * @return the debug
     */
    public static boolean isDebug() 
    {
        return debug;
    }

    /**
     * @param aDebug the debug to set
     */
    public static void setDebug(boolean aDebug) 
    {
        debug = aDebug;
    }

    public static void setMarcFile(String marcFilePath) 
    {
        marcFile = marcFilePath;
    }
    
    public static String getMarcFile()
    {
        return marcFile;
    }

    static void setOutputOnChangeOnly(boolean parseBoolean) 
    {
        outputOnChangeOnly = parseBoolean;
    }

    static boolean isOutputOnChangeOnly() 
    {
        return outputOnChangeOnly;
    }

    static void incrementWrittenRecords() 
    {
        writtenRecords++;
    }
    
    static void incrementPrintedRecords() 
    {
        printedRecords++;
    }

    static int getPrintedRecords() 
    {
        return printedRecords;
    }

    static int getWrittenRecords() 
    {
        return writtenRecords;
    }
    
    static void set(Variable v)
    {
        symbolTable.putAll(v.symbolTable);
    }
    
    static String get(String variableName)
    {
        return symbolTable.getOrDefault(variableName, "");
    }
    
    private Environment(){}    
}
