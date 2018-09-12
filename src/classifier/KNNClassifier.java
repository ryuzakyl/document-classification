package classifier;


import category.CategoriesSet;
import category.ICategory;
import document.IDocument;
import feature.IFeatureSelector;
import org.apache.lucene.index.TermFreqVector;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class KNNClassifier implements IClassifier
{
    private int k;

    private ArrayList<IDocument> learningDocuments;

    private ArrayList<ICategory> categories;

    public KNNClassifier(int k)
    {
        this.k = k;

        learningDocuments = new ArrayList<IDocument>();

        categories = new ArrayList<ICategory>();
    }

    public KNNClassifier()
    {
        //por defecto vamos a poner K=3
        this.k = 3;

        learningDocuments = new ArrayList<IDocument>();

        categories = new ArrayList<ICategory>();
    }

    @Override
    public void train(CategoriesSet set, IFeatureSelector selector) throws Exception
    {
        learningDocuments = new ArrayList<IDocument>();
        categories = new ArrayList<ICategory>();

        //llenamos los documentos de la fase de aprendizaje
        for(ICategory cat: set.getCategories())
        {
            categories.add(cat);

            for(IDocument doc: cat.GetDocuments())
                learningDocuments.add(doc);
        }
    }

    @Override
    public ICategory predict(IDocument document)
    {
        //voy a hacerlo a lo feo

        int n = learningDocuments.size();

        double[] distances = new double[n];

        //llenamos las distancias
        for(int i = 0; i < n; i++)
        {
            TermFreqVector xtfv = document.GetFrequencyVector();
            TermFreqVector ytfv = learningDocuments.get(i).GetFrequencyVector();

            String[] xTerms = new String[0];
            String[] yTerms = new String[0];

            int[] xFreqs = new int[0];
            int[] yFreqs = new int[0];

            if (xtfv != null)
            {
                xTerms = xtfv.getTerms();
                xFreqs = xtfv.getTermFrequencies();
            }

            if (ytfv != null)
            {
                yTerms = ytfv.getTerms();
                yFreqs = ytfv.getTermFrequencies();
            }

            double d = Utils.EuclideanDistance(xTerms, xFreqs, yTerms, yFreqs);

            distances[i] = d;
        }


        //hacemos el insertion sort
        for(int i = 0; i < n - 1; i++)
        {
            for(int j = i + 1; j < n; j++)
            {
                if (distances[i] > distances[j])
                {
                    double tmp = distances[i];
                    IDocument tmpDoc = learningDocuments.get(i);

                    distances[i] = distances[j];
                    learningDocuments.set(i, learningDocuments.get(j));

                    distances[j] = tmp;
                    learningDocuments.set(j, tmpDoc);
                }
            }
        }

        //obtenemos los k primeros
        List<IDocument> neighbours = learningDocuments.subList(0, k);

        ICategory bestCategory = null;
        int currScore, bestScore = -1;

        //recorremos las categorias
        for(ICategory currCategory: categories)
        {
            currScore = 0;

            //recorremos los documentos
            for(IDocument idoc: currCategory.GetDocuments())
            {
                //verificamos que esten entre los k primeros
                for(IDocument v: neighbours)
                {
                    if (v.GetDocumentId().equals(idoc.GetDocumentId()))
                        currScore++;
                }
            }

            //actualizamos la mejor
            if (currScore > bestScore)
            {
                bestScore = currScore;
                bestCategory = currCategory;
            }
        }

        return bestCategory;
    }
}
