package classifier;

import category.CategoriesSet;
import category.ICategory;
import document.IDocument;
import feature.IFeatureSelector;

public interface IClassifier
{
    //inicia la fase de entrenamiento del clasificador
    void train(CategoriesSet set, IFeatureSelector selector) throws Exception;



    //realiza la clasificacion de un documento
    ICategory predict(IDocument document);
}
