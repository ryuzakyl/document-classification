import category.CategoriesSet;
import category.Category;
import category.ICategory;
import classifier.*;
import classifier.NeuralNetwork.NeuralNetClassifier;
import document.IDocument;
import engine.CollectionEngine;
import engine.ICollectionEngine;
import evaluation.EvaluationMeasures;
import feature.*;
import org.apache.lucene.store.Directory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            ArrayList<String> sources = new ArrayList<String>();
            sources.add(".\\reuter");
            String indexPath = ".\\index";

            ICollectionEngine ce = new CollectionEngine(sources, indexPath);
            System.out.println("Indexing done");
            String testCollectionPath = sources.get(0);

            double avg_boost = 0.0;
            double avg_nb    = 0.0;
            for (int i = 0; i < 10; i++)
            {
                //obtenemos todos los documentos
                ArrayList<IDocument> collectionDocs = ce.GetCollectionDocuments();
                int trainingLength = 90;
                ArrayList<IDocument> trainingDocs = GetTrainingDocuments(collectionDocs, trainingLength);
                ArrayList<IDocument> testingDocs = GetTestingDocuments(collectionDocs, trainingDocs);
                int docsCount = ce.GetDocumentsCount();
                TreeMap<String, ArrayList<String>> categoryDocumentRelationship = GetCategoryDocumentRelationship(testCollectionPath, docsCount);
                ICategory[] trainingCategories = BuildTrainingCategories(trainingDocs, categoryDocumentRelationship);
                ArrayList<String> testingSet = BuildExpectedTestingCategoriesName(testingDocs, categoryDocumentRelationship);

                //Boosting Model base in NBClassifiers
                System.out.println("!!!!!!!!! Boosting model !!!!!!!!!");
                ArrayList<IClassifier> baseClassifiers = new ArrayList<IClassifier>();
                for (int j = 0; j < 5; j++)
                    baseClassifiers.add(new NaiveBayesClassifier());
                AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier(baseClassifiers);

                System.out.println("Begin training");
                CategoriesSet catSetBoost = new CategoriesSet(trainingCategories, ce);
                IFeatureSelector boostSelector = new None();
                adaBoostClassifier.train(catSetBoost, boostSelector);
                System.out.println("Begin classifying");
                ArrayList<String> resultBoost = BuildRealCategoriesName(adaBoostClassifier, testingDocs);
                avg_boost += EvaluationMeasures.CrossValidation(resultBoost, testingSet, testingDocs, testingDocs.size());
                System.out.println("Finished classifying");
                System.out.println();

                //Naive Bayes Classifier
                System.out.println("!!!!!!!!! Boosting model !!!!!!!!!");
                NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();

                System.out.println("Begin training");
                CategoriesSet catSetNB = new CategoriesSet(trainingCategories, ce);
                IFeatureSelector nbSelector = new None();
                naiveBayesClassifier.train(catSetNB, nbSelector);
                System.out.println("Begin classifying");
                ArrayList<String> resultNB = BuildRealCategoriesName(naiveBayesClassifier, testingDocs);
                avg_nb += EvaluationMeasures.CrossValidation(resultNB, testingSet, testingDocs, testingDocs.size());
                System.out.println("Finished classifying");
                System.out.println();



            }
            avg_boost = avg_boost / 10.0;
            avg_nb    = avg_nb / 10.0;

            System.out.println("------------------------------------------------------------------");
            System.out.println("Classifier Boost: " + avg_boost + "%");
            System.out.println("Classifier Naive Bayes: " + avg_nb + "%");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static  ArrayList<String> BuildRealCategoriesName(IClassifier classifier, ArrayList<IDocument> iDocuments)
    {
        ArrayList<String> result = new ArrayList<String>();
        String cName;
        for(IDocument d: iDocuments)
        {
           cName = classifier.predict(d).GetName();
           result.add(cName);
        }
        return result;
    }

    private static ArrayList<String> BuildExpectedTestingCategoriesName(ArrayList<IDocument> iDocuments, TreeMap<String, ArrayList<String>> categoryDocumentRelationship)
    {
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> categoriesName = GetCategoriesName(categoryDocumentRelationship);
        String cName = "";
        for(int i = 0; i < iDocuments.size(); i++)
        {
            cName = AssignExpectedCategoryName(categoryDocumentRelationship, categoriesName, iDocuments.get(i));
            result.add(cName);
        }
        return result;
    }

    private static String AssignExpectedCategoryName(TreeMap<String, ArrayList<String>> categoryDocumentRelationship, ArrayList<String> categoriesName, IDocument document)
    {
        String result = "";
        ArrayList<String> documentsNameByCategory = null;
        for(String cName:categoriesName)
        {
            documentsNameByCategory = categoryDocumentRelationship.get(cName);
            if(documentsNameByCategory.contains(document.GetDocumentId())) return cName;
        }
        return "";
    }

    private static ArrayList<String> GetCategoriesName(TreeMap<String, ArrayList<String>> categoryDocumentRelationship)
    {
        ArrayList<String> result = new ArrayList<String>();
        Iterator<String> iterator = categoryDocumentRelationship.keySet().iterator();
        while(iterator.hasNext())
        {
            result.add(iterator.next());
        }
        return result;
    }

    private static ICategory[] BuildTrainingCategories(ArrayList<IDocument> iDocuments, TreeMap<String, ArrayList<String>> categoryDocumentRelationship) throws IOException
    {
        ArrayList<ICategory> result = new ArrayList<ICategory>();

        for (String entryKey : categoryDocumentRelationship.keySet()) {
            ArrayList<String> entryValue = categoryDocumentRelationship.get(entryKey);
            ArrayList<IDocument> documentsByCategory = new ArrayList<IDocument>();
            for (int i = 0; i < entryValue.size(); i++) {
                if (DocumentNameBelongsToDocumentsList(iDocuments, entryValue.get(i)))
                    documentsByCategory.add(GetDocument(iDocuments, entryValue.get(i)));
            }
            if (documentsByCategory.size() > 0) {
                IDocument[] documents = documentsByCategory.toArray(new IDocument[documentsByCategory.size()]);
                Category c = new Category(entryKey, documents);
                result.add(c);
            }
        }
        return result.toArray(new ICategory[result.size()]);
    }

    public static IDocument GetDocument(ArrayList<IDocument> documents, String documentId)
    {
        for (IDocument document : documents) {
            if (document.GetDocumentId().equals(documentId)) return document;
        }
        return null;
    }

    public static boolean DocumentNameBelongsToDocumentsList(ArrayList<IDocument> documents, String documentId)
    {
        for (IDocument document : documents) {
            if (document.GetDocumentId().equals(documentId)) return true;
        }
        return false;
    }

    public static ArrayList<IDocument> GetTestingDocuments(ArrayList<IDocument> collection, ArrayList<IDocument> testing)
    {
        ArrayList<IDocument> result = new ArrayList<IDocument>();
        for (IDocument aCollection : collection) if (!testing.contains(aCollection)) result.add(aCollection);
        return result;
    }

    public static ArrayList<IDocument> GetTrainingDocuments(ArrayList<IDocument> collection, int k)
    {
        //obtenemos el count de documentos
        int currEmptySpots = collection.size();

        //creamos el array de marcas
        boolean[] marks = new boolean[currEmptySpots];

        ArrayList<IDocument> result = new ArrayList<IDocument>();
        Random r = new Random();
        int index;

        for(int i = 0; i < k; i++)
        {
            index = r.nextInt(currEmptySpots);

            int cont = 0;
            for(int j = 0; j < marks.length; j++)
            {
                if (!marks[j])
                {
                    if (cont == index)
                    {
                        result.add(collection.get(j));
                        marks[j] = true;
                        break;
                    }
                    cont++;
                }
            }

            currEmptySpots--;
        }

        return result;
    }

    public static TreeMap<String, ArrayList<String>> GetCategoryDocumentRelationship(String docsPath, int docsCount) throws IOException
    {
        TreeMap<String, ArrayList<String>> categories = new TreeMap<String, ArrayList<String>>();

        //File f = new File(docsPath + "\\reut2.info");
        //BufferedReader reader = new BufferedReader(new FileReader(docsPath + "\\reut2.info"));

        BufferedReader reader = new BufferedReader(new FileReader(new File(docsPath + "\\reut2.info")));

        int cont = 0;

        while(cont < docsCount)
        {
            String fileId = splitRemoveEmptyEntries(reader.readLine(), " ")[1];
            String docId = (Integer.parseInt(fileId, 10) - 1) + "";

            reader.readLine(); // title ignored

            String[] topicList = splitRemoveEmptyEntries(reader.readLine(), "[,: ]");

            reader.readLine(); // places ignored
            reader.readLine(); // people ignored
            reader.readLine(); // orgs ignored
            reader.readLine(); // exchanges ignored
            reader.readLine(); // companies ignored
            reader.readLine(); // blank line ignored

            if (topicList.length > 1)
            {
                if (!categories.containsKey(topicList[1]))
                    categories.put(topicList[1], new ArrayList<String>());
                categories.get(topicList[1]).add(docId);
                cont++;
            }
        }

        return categories;
    }

    private static String[] splitRemoveEmptyEntries(String s, String regex)
    {
        String[] r = s.split(regex);

        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < r.length; i++)
        {
            if (r[i].compareTo("") != 0 && r[i].compareTo(" ") != 0)
                result.add(r[i]);
        }

        return result.toArray(new String[result.size()]);
    }
}
