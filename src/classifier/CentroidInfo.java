package classifier;

import document.IDocument;
import org.apache.lucene.index.TermFreqVector;

import java.util.Set;
import java.util.TreeMap;

public class CentroidInfo
{
    TreeMap<String, Integer> frecs;
    TreeMap<String, Integer> counts;

    public CentroidInfo()
    {
        frecs = new TreeMap<String, Integer>();
        counts = new TreeMap<String, Integer>();
    }

    public void AddDocument(IDocument doc)
    {
        TermFreqVector tfv = doc.GetFrequencyVector();

        if (tfv == null)
            return;

        String[] terms = tfv.getTerms();
        int[] freqs = tfv.getTermFrequencies();

        for(int i = 0; i < terms.length; i++)
        {
            if (!frecs.keySet().contains(terms[i]))
            {
                frecs.put(terms[i], freqs[i]);
                counts.put(terms[i], 1);
            }
            else
            {
                Integer currFreq = frecs.get(terms[i]);
                Integer currCount = counts.get(terms[i]);

                frecs.put(terms[i], currFreq + freqs[i]);
                counts.put(terms[i], currCount + 1);
            }
        }
    }

    public String[] GetTerms()
    {
        Set<String> terms = frecs.keySet();
        String[] result = new String[terms.size()];

        int i = 0;
        for(String t: terms)
        {
            result[i] = t;
            i++;
        }

        return result;
    }

    public int[] GetFrequencies()
    {
        Set<String> terms = frecs.keySet();
        int[] result = new int[terms.size()];

        int i = 0;
        for(String t: terms)
        {
            result[i] = frecs.get(t) / counts.get(t);
        }

        return result;
    }
}
