/*
 * Copyright 2015 Andrew Nisbet.
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

/**
 *
 * @author Andrew Nisbet
 */
public class Histogram
{
    private static int MAXIMUM;
    public Histogram(int maximum)
    {
        MAXIMUM = maximum;
    }
    
    public String get(String label, int count)
    {
        StringBuilder sb = new StringBuilder();
        double peak = Math.floor(((double)count / (double)MAXIMUM) * 50.0);
        int m = (int)peak;
        String formattedLabel = String.format("%10s |",label);
        sb.append(formattedLabel);
        for (int i = 0; i < 50; i++)
        {
            if (i <= m)
            {
                sb.append("*");
            }
            else
            {
                sb.append("-");
            }
        }
        sb.append("|");
        return sb.toString();
    }
}
