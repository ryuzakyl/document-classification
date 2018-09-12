package feature;

import category.CategoriesSet;
import category.ICategory;

import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 12/28/12
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IFeatureSelector {

    public TreeSet<String> Filter(ICategory category, CategoriesSet catSet, int k);
}
