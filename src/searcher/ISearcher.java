package searcher;

import document.IDocument;
import java.io.IOException;
import java.util.ArrayList;

public interface ISearcher
{
    //setea el path del indice
    void SetIndexPath(String indexPath) throws Exception;

    //obtiene el path del index
    String GetIndexPath();

    //obtiene los documentos indexados
    ArrayList<IDocument> GetIndexedDocuments() throws IOException;

    //obtiene la cantidad de codumentos indexados
    int GetIndexedDocumentsCount() throws IOException;

    //obtiene un documento dado el id
    IDocument GetDocument(int documentId) throws IOException;

    //obtiene los terminos indexados
    String[] GetIndexedTerms() throws IOException;

    //obtiene los terminos de un documento
    String[] GetIndexedTerms(int documentId) throws IOException;

    int GetTermFrecuencyInDocument(int documentId, String term) throws IOException;
}
