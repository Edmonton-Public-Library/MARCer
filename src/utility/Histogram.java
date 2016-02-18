/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
