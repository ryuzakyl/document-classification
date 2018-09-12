package classifier;

import category.CategoriesSet;
import category.ICategory;
import document.IDocument;
import feature.IFeatureSelector;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/10/13
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class BoostingClassifier implements IClassifier
{


    @Override
    public void train(CategoriesSet set, IFeatureSelector selector) throws Exception
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ICategory predict(IDocument document)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
