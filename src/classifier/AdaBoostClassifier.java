package classifier;

import category.CategoriesSet;
import category.Category;
import category.ICategory;
import document.IDocument;
import feature.IFeatureSelector;
import utils.KeyValuePair;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: DaniMesejo
 * Date: 1/14/13
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class AdaBoostClassifier implements IClassifier
{
    ArrayList<IClassifier> classifiers;
    private static Random roulette = new Random(System.currentTimeMillis());
    private double[] HypWeights;

    public AdaBoostClassifier(ArrayList<IClassifier> classifiers)
    {
        this.classifiers = new ArrayList<IClassifier>();

        int classifiersCount = classifiers.size();

        for(IClassifier classifier: classifiers)
        {
            this.classifiers.add(classifier);
        }

        //creamos los pesos de las hipotesis
        HypWeights = new double[classifiersCount];
    }

    @Override
    public void train(CategoriesSet set, IFeatureSelector selector) throws Exception
    {
        ArrayList<IDocument> inputs = new ArrayList<IDocument>();
        ArrayList<ICategory> answers = new ArrayList<ICategory>();

        for(ICategory ic: set.getCategories())
        {
            for(IDocument idoc: ic.GetDocuments())
            {
                inputs.add(idoc);
                answers.add(ic);
            }
        }

        int N = inputs.size();
        double[] w = new double[N];
        ResetWeights(w);

        //por cada uno de los clasificadores
        for (int i = 0; i < classifiers.size(); i++)
        {
            //escojo la muestra a entrenar
            KeyValuePair<List<IDocument>, List<ICategory>> pop_sample = GetPopulationSample(inputs, answers, w);
            ICategory[] categories = buildCategories(pop_sample.getKey(), pop_sample.getValue());
            CategoriesSet categoriesSet = new CategoriesSet(categories, set.getDocumentManager());

            //entrenamos el clasificador
            //classifiers.get(i).train(pop_sample.getKey(), pop_sample.getValue());
            classifiers.get(i).train(categoriesSet, selector);

            //aqui almacenare si clasifico bien los elementos
            boolean[] ok_classified = new boolean[N];

            double error = 0.0;
            IClassifier classifier;
            for (int j = 0; j < N; j++)
            {
                classifier = classifiers.get(i);
                ICategory category = classifier.predict(inputs.get(j));
                if(!category.GetName().equals(answers.get(j)))
                    error += w[j];
                else
                    ok_classified[j] = true;
            }

            if (error == 0.0 || error >= 0.5)
            {
                ResetWeights(w);
                continue;
            }

            for (int j = 0; j < N; j++)
            {
                if (ok_classified[j])
                {
                    w[j] *= (error / (1 - error));
                }
            }

            Normalize(w);
            HypWeights[i] = -Math.log10((1 - error) / error);
        }
    }

    private IDocument[] getCategoryDocuments(ICategory category, List<IDocument> documents, List<ICategory> categories)
    {
        ArrayList<IDocument> result = new ArrayList<IDocument>();
        for (int i = 0; i < documents.size(); i++)
        {
            if(categories.get(i).GetName().equals(category.GetName())) result.add(documents.get(i));
        }
        return result.toArray(new IDocument[result.size()]);
    }



    private ICategory[] buildCategories(List<IDocument> documents, List<ICategory> categories) throws IOException
    {
        TreeSet<String> cNames = new TreeSet<String>();
        ArrayList<ICategory> result = new ArrayList<ICategory>();

        for (int i = 0; i < categories.size(); i++)
        {
            if(!cNames.contains(categories.get(i).GetName()))
            {
                cNames.add(categories.get(i).GetName());
                IDocument[] cDocuments = getCategoryDocuments(categories.get(i), documents, categories);
                result.add(new Category(categories.get(i).GetName(), cDocuments));
            }
        }

        return result.toArray(new ICategory[result.size()]);
    }


    @Override
    public ICategory predict(IDocument document)
    {
        TreeMap<ICategory, Double> weighted_majority = new TreeMap<ICategory, Double>();

        for (int i = 0; i < HypWeights.length; i++)
        {
            ICategory classification = classifiers.get(i).predict(document);

            if (weighted_majority.containsKey(classification))
            {
                double currWeight = weighted_majority.get(classification);
                weighted_majority.put(classification, currWeight + HypWeights[i]);
            }
            else
                weighted_majority.put(classification, HypWeights[i]);
        }

        ICategory prediction = null;
        double predictionWeight = -1000;

        for(ICategory current: weighted_majority.keySet())
        {
            Double currentWeight = weighted_majority.get(current);
            if (currentWeight > predictionWeight)
            {
                predictionWeight = currentWeight;
                prediction = current;
            }
        }
        return prediction;
    }

    private void ResetWeights(double[] w) {
        int N = w.length;
        for(int i = 0; i < N; i++)
            w[i] = 1.0 / N;
    }

    private void Normalize(double[] w) {
        double sum = 0.0;

        for (int k = 0; k < w.length; k++)
            sum += w[k];

        for (int i = 0; i < w.length; i++)
            w[i] /= sum;
    }

    private KeyValuePair<List<IDocument>, List<ICategory>> GetPopulationSample(List<IDocument> inputs, List<ICategory> answers, double[] w)
    {
        ArrayList<IDocument> input_sample = new ArrayList<IDocument>();
        ArrayList<ICategory> answers_sample = new ArrayList<ICategory>();

        while (input_sample.size() == 0)
        {
            for(int i = 0; i < inputs.size(); i++)
            {
                // los escojo segun la probabilidad dada por w
                if (w[i] < roulette.nextDouble())
                {
                    input_sample.add(inputs.get(i));
                    answers_sample.add(answers.get(i));
                }
            }
        }

        return new KeyValuePair<List<IDocument>, List<ICategory>>(input_sample , answers_sample);
    }
}
