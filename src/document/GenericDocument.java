package document;

import org.apache.lucene.index.TermFreqVector;

public class GenericDocument implements IDocument
{
    //id del documento
    private String id;
    private String url;
    private TermFreqVector freqVector;

    public GenericDocument()
    {

    }

    public GenericDocument(String id)
    {
        this.id = id;
    }

    public GenericDocument(String id, TermFreqVector fVector){
        this.id = id;
        freqVector = fVector;
    }

    @Override
    public String GetDocumentId()
    {
        return this.id;
    }

    @Override
    public void SetDocumentId(String newId)
    {
        this.id = newId;
    }

    @Override
    public String GetDocumentUrl()
    {
        return url;
    }

    @Override
    public void SetDocumentUrl(String newUrl)
    {
        url = newUrl;
    }

    @Override
    public TermFreqVector GetFrequencyVector() {
        return freqVector;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsTerm(String term) {
        if(freqVector == null) return false;
        for(int i = 0; i < freqVector.getTerms().length; i++)
            if(freqVector.getTerms()[i].compareTo(term) == 0) return true;
        return false;
    }
}
