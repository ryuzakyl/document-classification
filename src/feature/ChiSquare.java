package feature;

import category.CategoriesSet;
import category.ICategory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

public class ChiSquare implements IFeatureSelector {

    @Override
    public TreeSet<String> Filter(ICategory category, CategoriesSet catSet, int k){
        Comparator<Double> c = new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2)* -1;
            }
        };

        TreeSet<String> terms = new TreeSet<String>();
        TreeMap<Double,String> frequency = new TreeMap<Double, String>(c);

        TreeMap<String,Integer> termsOcc = catSet.getTermOccurrences();
        for(String s:catSet.getVocabulary())
        {
            if(category.GetDocumentCountPerTerm(s) > 0)
            {
                double N11,N10,N01,N00,N;
                N11 = category.GetDocumentCountPerTerm(s);
                N10 =  termsOcc.get(s) - N11;
                N01 = category.GetDocumentCount() - category.GetDocumentCountPerTerm(s);
                N00 = catSet.getDocumentCount() - termsOcc.get(s) - category.GetDocumentCount() + category.GetDocumentCountPerTerm(s);
                N = N00 + N01 + N10 + N11;

                double X =  N * Math.pow((N11*N00 - N01*N10),2)/(N11 + N01) * (N11 + N10) * (N10 + N00) * (N01 + N00);
                frequency.put(X,s);
            }
        }
        String[] mapTerms = new String[frequency.size()];
        frequency.values().toArray(mapTerms);
        terms.addAll(Arrays.asList(mapTerms).subList(0, Math.min(k, frequency.size())));
        return terms;
    }
}
