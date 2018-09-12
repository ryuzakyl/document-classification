package utils;

public class Utils
{
    public static double EuclideanDistance(String[] xTerms, int[] xFreqs, String[] yTerms, int[] yFreqs)
    {
        double distance = 0.0;

        int n1 = xTerms.length;
        int n2 = yTerms.length;

        int i = 0;
        int j = 0;
        int cmp, dif;
        while (i < n1 && j < n2)
        {
            cmp = xTerms[i].compareTo(yTerms[j]);

            if (cmp < 0)
            {
                distance += xFreqs[i] * xFreqs[i];
                i++;
            }
            else if (cmp > 0)
            {
                distance += yFreqs[j] * yFreqs[j];
                j++;
            }
            else
            {
                dif = Math.abs(xFreqs[i] - yFreqs[j]);
                distance += dif * dif;

                i++;
                j++;
            }
        }

        for(; i < n1; i++)
            distance += xFreqs[i] * xFreqs[i];

        for(; j < n2; j++)
            distance += yFreqs[j] * yFreqs[j];

        distance = Math.sqrt(distance);
        return distance;
    }
}
