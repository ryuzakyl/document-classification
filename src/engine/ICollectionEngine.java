package engine;

import document.IDocument;

import java.io.IOException;
import java.util.ArrayList;

public interface ICollectionEngine
{
    //obtiene la lista de extensiones de archivos soportadas
    ArrayList<String> GetSupportedExtensions();

    //setea la lista de extensiones de archivos soportadas
    void SetSupportedExtensions(ArrayList<String> newFilter) throws Exception;

    //construye el indice de la coleccion
    void BuildIndex();

    //obtiene la cantidad de codumentos indexados
    int GetDocumentsCount();

    //obtiene la colleccion de documentos
    ArrayList<IDocument> GetCollectionDocuments();

    //obtiene todos los términos de la colleccion
    String[] GetCollectionTerms();

    //obtiene los terminos de un documento dado
    String[] GetDocumentTerms(IDocument document);

    //obtiene los terminos de un documento dado su Id
    String[] GetDocumentTerms(String documentId);

    //obtiene un documento dado su id
    IDocument GetDocumentById(String documentId);

    //obtiene la frecuancia de un termino en un documento
    double GetTermFrequencyInDocument(IDocument document, String term);

    //obtiene la frecuencia de un termino en un documento dado su Id
    double GetTermFrequencyInDocument(String documentId, String term);
}
