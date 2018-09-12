package feature;

import category.CategoriesSet;
import category.ICategory;

import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/2/13
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class None implements IFeatureSelector {
    @Override
    public TreeSet<String> Filter(ICategory category, CategoriesSet catSet, int k) {
        return new TreeSet<String>(catSet.getVocabulary());
    }
}
