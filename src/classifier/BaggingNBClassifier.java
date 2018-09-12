package classifier;

import category.CategoriesSet;
import category.Category;
import category.ICategory;
import document.IDocument;
import feature.IFeatureSelector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/10/13
 * Time: 7:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class BaggingNBClassifier implements IClassifier {

    NaiveBayesClassifier[] baseClassifiers;

    public BaggingNBClassifier(int k)
    {
        baseClassifiers = new NaiveBayesClassifier[k];
        for (int i = 0; i < k; i++) {
            baseClassifiers[i] = new NaiveBayesClassifier();
        }
    }

    @Override
    public void train(CategoriesSet set, IFeatureSelector selector) throws Exception
    {
        CategoriesSet baseSet = null;
        for (int i = 0; i < baseClassifiers.length; i++)
        {
           baseSet = new CategoriesSet(getSampleCategories(set.getCategories()), set.getDocumentManager());
           baseClassifiers[i].train(baseSet, selector);
        }
    }

    private ICategory[] getSampleCategories(ICategory[] categories) throws  IOException
    {
        ICategory[] result = new ICategory[categories.length];
        for (int i = 0; i < categories.length; i++)
            result[i] = sampleCategory(categories[i]);
        return result;
    }

    private ICategory sampleCategory(ICategory input) throws IOException
    {
        int idx = 0;
        Random random = new Random();
        IDocument[] documents = new IDocument[input.GetDocuments().length];

        for (int i = 0; i < documents.length; i++)
        {
            idx = random.nextInt(documents.length);
            documents[i] = input.GetDocuments()[idx];
        }

        ICategory result = new Category(input.GetName(), documents);
        return result;
    }

    @Override
    public ICategory predict(IDocument document)
    {
        ICategory[] partialResult = new ICategory[baseClassifiers.length];
        TreeMap<String, Integer> votes = new TreeMap<String, Integer>();

        String cName = ""; int cVote = 0;
        for (int i = 0; i < partialResult.length; i++)
        {
            partialResult[i] = baseClassifiers[i].predict(document);
            cName = partialResult[i].GetName();
            if(votes.containsKey(cName))
            {
                cVote = votes.remove(cName);
                votes.put(cName, cVote + 1);
            }
            else
            {
                votes.put(cName, 1);
            }
        }
        cName = argMax(votes);

        ICategory result = null;
        for (int i = 0; i < partialResult.length; i++)
        {
            if(partialResult[i].GetName().equals(cName))
            {
                result = partialResult[i];
            }
        }
        return result;
    }

    private String argMax(TreeMap<String,Integer> votes)
    {
        String result = "";
        int max = -1;
        for(String k: votes.keySet())
        {
            if(votes.get(k) > max)
            {
                result = k;
                max = votes.get(k);
            }
        }
        return result;
    }
}
