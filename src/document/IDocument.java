package document;

import org.apache.lucene.index.TermFreqVector;

public interface IDocument
{
    //obtiene el id del documento
    String GetDocumentId();

    //setea el id del documento
    void SetDocumentId(String newId);

    //obtiene la url del documento
    String GetDocumentUrl();

    //setea la url del documento
    void SetDocumentUrl(String newUrl);

    //obtiene un vector de termino frecuencia
    TermFreqVector GetFrequencyVector();

    //determina si se contiene un termino
    boolean containsTerm(String term);
}
