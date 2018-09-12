package classifier;

import category.CategoriesSet;
import category.ICategory;
import document.IDocument;
import feature.IFeatureSelector;
import org.apache.lucene.index.TermFreqVector;
import utils.Utils;

import java.util.ArrayList;

public class RocchioClassifier implements IClassifier
{
    private ArrayList<ICategory> categories;

    private ArrayList<CentroidInfo> centroids;

    public RocchioClassifier()
    {
        categories = new ArrayList<ICategory>();

        centroids = new ArrayList<CentroidInfo>();
    }

    @Override
    public void train(CategoriesSet set, IFeatureSelector selector) throws Exception
    {
        //llenamos los documentos de la fase de aprendizaje
        ICategory[] cats = set.getCategories();
        for(int i = 0; i < cats.length; i++)
        {
            categories.add(cats[i]);
            centroids.add(new CentroidInfo());

            IDocument[] docs = cats[i].GetDocuments();
            for(int j = 0; j < docs.length; j++)
            {
                //llenamos la informacion de los centroides
                centroids.get(i).AddDocument(docs[j]);
            }
        }
    }

    @Override
    public ICategory predict(IDocument document)
    {
        double[] distances = new double[categories.size()];

        for(int i = 0; i < distances.length; i++)
        {
            TermFreqVector xtfv = document.GetFrequencyVector();

            String[] xTerms = new String[0];
            int[] xFreqs = new int[0];

            CentroidInfo currCentroid = centroids.get(i);
            String[] yTerms = currCentroid.GetTerms();
            int[] yFreqs = currCentroid.GetFrequencies();

            if (xtfv != null)
            {
                xTerms = xtfv.getTerms();
                xFreqs = xtfv.getTermFrequencies();
            }

            double d = Utils.EuclideanDistance(xTerms, xFreqs, yTerms, yFreqs);
            distances[i] = d;
        }

        double bestScore = 1000000.0;
        int bestIndex = -1;

        //encontramos la mejor categoria
        for(int k = 0; k < distances.length; k++)
        {
            if (distances[k] < bestScore)
            {
                bestScore = distances[k];
                bestIndex = k;
            }
        }

        //actualizamos los centroides
        centroids.get(bestIndex).AddDocument(document);

        return categories.get(bestIndex);
    }
}
