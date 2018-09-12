package category;

import engine.ICollectionEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 12/27/12
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class CategoriesSet {

    private List<String> vocabulary;
    private ICollectionEngine documentManager;
    private TreeMap<String, Integer> termFrequencies;
    private TreeMap<String, Integer> termOccurrences;
    private ICategory[] categories;
    private int documentCount;

    public CategoriesSet(ICategory[] categories, ICollectionEngine dManager) {
        documentManager = dManager;
        String[] v = documentManager.GetCollectionTerms();
        vocabulary = new ArrayList<String>();
        for (int i = 0; i < v.length; i++) {
            vocabulary.add(v[i]);
        }
        createOccurrencesAndFrequenciesTable(categories);
        this.categories = categories;

        for(int i = 0; i < v.length; i++) {
            if(termOccurrences.get(v[i]) <= 0) vocabulary.remove(v[i]);
        }

        documentCount = 0;
        for (int i = 0; i < categories.length; i++) {
            documentCount += categories[i].GetDocumentCount();
        }
    }

    public CategoriesSet() {
        vocabulary = new ArrayList<String>();
        documentManager = null;
        termFrequencies = new TreeMap<String, Integer>();
        termOccurrences = new TreeMap<String, Integer>();
    }

    public List<String> getVocabulary() {
        return vocabulary;
    }

    public ICollectionEngine getDocumentManager() {
        return documentManager;
    }

    public TreeMap<String, Integer> getTermFrequencies() {
        return termFrequencies;
    }

    public TreeMap<String, Integer> getTermOccurrences() {
        return termOccurrences;
    }

    public ICategory[] getCategories(){
        return categories;
    }

    public int getDocumentCount() {
        return documentCount;
    }

    private void createOccurrencesAndFrequenciesTable(ICategory[] categories) {
        termFrequencies = new TreeMap<String, Integer>();
        termOccurrences = new TreeMap<String, Integer>();

        int tmpFreq, tmpOcur;
        for(String term : vocabulary)
        {
            tmpFreq = 0; tmpOcur = 0;
            for(ICategory category : categories)
            {
                tmpFreq += category.GetFrequencyPerTerm(term);
                tmpOcur += category.GetDocumentCountPerTerm(term);
            }
            termFrequencies.put(term, tmpFreq);
            termOccurrences.put(term, tmpOcur);
        }
    }


}
