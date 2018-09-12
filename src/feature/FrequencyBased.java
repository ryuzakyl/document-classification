package feature;

import category.CategoriesSet;
import category.ICategory;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 12/28/12
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class FrequencyBased implements IFeatureSelector
{
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
        for(String s:catSet.getVocabulary())
            frequency.put(category.GetFrequencyPerTerm(s),s);
        String[] mapTerms = new String[frequency.size()];
        frequency.values().toArray(mapTerms);
        for(int i = 0; i < Math.min(k,frequency.size()); i++)
            terms.add(mapTerms[i]);
        return terms;
    }
}
