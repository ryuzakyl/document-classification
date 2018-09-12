package classifier;

import category.CategoriesSet;
import category.ICategory;
import document.IDocument;
import feature.IFeatureSelector;

import java.util.TreeSet;

public class BernoulliClassifier implements IClassifier
{
    CategoriesSet set;
    private double[] prior;
    private double[][] condprob;


    public BernoulliClassifier()
    {
        set = new CategoriesSet();
    }

    @Override
    public void train(CategoriesSet set, IFeatureSelector selector) throws Exception
    {
        this.set = set;
        ICategory[] categories = set.getCategories();
        int N = set.getDocumentCount();

        prior = new double[categories.length];
        condprob = new double[set.getVocabulary().size()][categories.length];

        for (int c = 0; c < set.getCategories().length; c++)
        {
            double Nc = categories[c].GetDocumentCount();
            prior[c] = Nc / N;

            TreeSet<String> features = selector.Filter(categories[c], set, 10);
            for (int t = 0; t < set.getVocabulary().size(); t++)
            {
                String term = set.getVocabulary().get(t);
                double Nct = 0;
                if(features.contains(term)) Nct = categories[c].GetDocumentCountPerTerm(term);
                condprob[t][c] = (Nct + 1.0) / (Nc + 2.0);
            }
        }
    }

    @Override
    public ICategory predict(IDocument document)
    {
        double[] score = new double[set.getCategories().length];

        for (int c = 0; c < set.getCategories().length; c++) {
            score[c] = Math.log(prior[c]);
            for (int j = 0; j < set.getVocabulary().size(); j++)
            {
                String term = set.getVocabulary().get(j);
                if(document.containsTerm(term))
                    score[c] += Math.log(condprob[j][c]);
                else
                    score[c] += Math.log(1 - condprob[j][c]);
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
