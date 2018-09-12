package category;

import document.IDocument;
import org.apache.lucene.index.TermFreqVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class Category implements ICategory, Comparable
{

    private String name;
    private ArrayList<IDocument> docs;
    private TreeMap<String, Integer> termFrequencies;
    private TreeMap<String, Integer> termOccurrences;
    double totalFrequency;

    public Category(String categoryName, IDocument... d) throws IOException
    {
        this.name = categoryName;
        docs = new ArrayList<IDocument>();
        Collections.addAll(docs, d);
        this.CreateOccurrencesAndFrequenciesTables();
        totalFrequency = 0;
        for(int e:termFrequencies.values())
        {
            totalFrequency = totalFrequency + e;
        }
    }

    @Override
    public IDocument[] GetDocuments() {
        return docs.toArray(new IDocument[docs.size()]);
    }

    @Override
    public double GetFrequencyPerTerm(String term) {
        if(termFrequencies.containsKey(term)) return termFrequencies.get(term);
        return 0;
    }

    @Override
    public double GetTotalFrequency() {
        return totalFrequency;
    }

    @Override
    public double GetDocumentCount() {
        return docs.size();
    }

    @Override
    public double GetDocumentCountPerTerm(String term) {
        if(termOccurrences.containsKey(term)) return termOccurrences.get(term);
        return 0;
    }

    @Override
    public String GetName() {
        return name;
    }

    public void CreateOccurrencesAndFrequenciesTables() throws IOException
    {
        termFrequencies = new TreeMap<String, Integer>();
        termOccurrences = new TreeMap<String, Integer>();
        for (IDocument doc : docs) {
            TermFreqVector temporal = doc.GetFrequencyVector();
            if (temporal != null) {
                for (int j = 0; j < temporal.getTerms().length; j++) {
                    if (!termFrequencies.containsKey(temporal.getTerms()[j])) {
                        termFrequencies.put(temporal.getTerms()[j], temporal.getTermFrequencies()[j]);
                        termOccurrences.put(temporal.getTerms()[j], 1);
                    } else {
                        int oldValue = termFrequencies.remove(temporal.getTerms()[j]);
                        termFrequencies.put(temporal.getTerms()[j], oldValue + temporal.getTermFrequencies()[j]);
                        oldValue = termOccurrences.remove(temporal.getTerms()[j]);
                        termOccurrences.put(temporal.getTerms()[j], oldValue + 1);
                    }
                }
            }
        }
    }

    @Override
    public int compareTo(Object o)
    {
        try
        {
            Category other = (Category)o;

            return this.GetName().compareTo(other.GetName());
        }
        catch (Exception e)
        {
            return -1;
        }
    }
}
