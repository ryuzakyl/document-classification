package classifier;

import category.CategoriesSet;
import category.ICategory;
import document.IDocument;
import feature.IFeatureSelector;

import java.util.TreeSet;

public class NaiveBayesClassifier implements IClassifier
{
    private double[] prior;
    private double[][] condprob;
    private CategoriesSet set;

    public NaiveBayesClassifier()
    {
        set = new CategoriesSet();
    }

    @Override
    public void train(CategoriesSet set, IFeatureSelector selector) throws Exception
    {
        this.set = set;
        ICategory[] categories = set.getCategories();

        double N = set.getDocumentCount(), Nc;
        prior = new double[categories.length];
        condprob = new double[set.getVocabulary().size()][categories.length];

        double B = set.getVocabulary().size(), Tct, TotalTct;
        for(int c = 0; c < categories.length; c++)
        {
            Nc = categories[c].GetDocumentCount();
            prior[c] = Nc/N;
            TotalTct = 0;
            TreeSet<String> features = selector.Filter(categories[c], set, 10);
            for (String feature : features) {
                TotalTct += categories[c].GetFrequencyPerTerm(feature);
            }

            for(int t = 0; t < B; t++)
            {
                Tct = 0;
                String term = set.getVocabulary().get(t);
                if(features.contains(term)) Tct = categories[c].GetFrequencyPerTerm(term);
                condprob[t][c] = (Tct + 1.0)/(TotalTct + B);
            }
        }
    }

    @Override
    public ICategory predict(IDocument document)
    {
        double[] score = new double[set.getCategories().length];
        for( int c = 0; c < set.getCategories().length; c++)
        {
            for(int t = 0; t < set.getVocabulary().size(); t++)
            {
                String term = set.getVocabulary().get(t);
                if(document.containsTerm(term))
                    score[c] += Math.log(condprob[t][c]);
            }
        }
        int c = ArgMax(score);
        return set.getCategories()[c];
    }

    private int ArgMax(double[] score)
    {
        int j = 0;
        for( int i = 0; i < score.length; i++)
        {
            if(score[i] > score[j])
            {
                j = i;
            }
        }
        return j;
    }
}
