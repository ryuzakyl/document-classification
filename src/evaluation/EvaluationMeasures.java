package evaluation;

import document.IDocument;

import java.util.ArrayList;

public class EvaluationMeasures
{
    public static double KFold(ArrayList<Double> partialErrors)
    {
        return 0.0;
    }

    public static double CrossValidation(ArrayList<String> result, ArrayList<String> expected, ArrayList<IDocument> testingDocuments, double testingDocumentsCount)
    {
        double error = 0;
        for	(int i = 0; i < result.size(); i++)
        {
            if(!result.get(i).equals(expected.get(i)))
            {
                //System.out.println("Document: " + testingDocuments.get(i).GetDocumentId() + " Expected: " + expected.get(i) + " Given: " + result.get(i));
                error += 1;
            }

        }
        return 1 - (error/testingDocumentsCount);
    }
}
