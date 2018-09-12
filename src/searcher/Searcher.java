package searcher;

import document.DocumentField;
import document.GenericDocument;
import document.IDocument;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.WildcardTermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Searcher implements ISearcher
{
    //path del indice
    private String indexPath;

    //encargado de buscar en el indice
    private IndexSearcher luceneSearcher;

    public Searcher(String indexPath) throws Exception
    {
        //validamos el directorio
        if (!ValidIndexPath(indexPath))
            throw new Exception("Invalid index path.");

        this.indexPath = indexPath;

        SetIndexReader(indexPath);
    }

    private boolean ValidIndexPath(String indexPath)
    {
        File indexDirectory = new File(indexPath);
        return indexDirectory.exists() && indexDirectory.isDirectory();
    }

    private void SetIndexReader(String indexPath) throws IOException
    {
        Directory indexDirectory = FSDirectory.open(new File(indexPath));
        luceneSearcher = new IndexSearcher(indexDirectory);
    }

    @Override
    public void SetIndexPath(String indexPath) throws Exception
    {
        this.indexPath = indexPath;
        SetIndexReader(indexPath);
    }

    @Override
    public String GetIndexPath()
    {
        return this.indexPath;
    }

    @Override
    public ArrayList<IDocument> GetIndexedDocuments() throws IOException
    {
        ArrayList<IDocument> result = new ArrayList<IDocument>();

        //obtenemos la cantidad de documentos de la coleccion
        int documentsCount = GetIndexedDocumentsCount();

        //obtenemos los documentos
        for(int i = 0; i < documentsCount; i++)
        {
            IDocument idoc = GetDocument(i);
            result.add(idoc);
        }

        return result;
    }

    @Override
    public int GetIndexedDocumentsCount() throws IOException
    {
        return luceneSearcher.maxDoc();
    }

    @Override
    public IDocument GetDocument(int documentId) throws IOException
    {
        Document doc = luceneSearcher.doc(documentId);

        String docId = doc.get(DocumentField.ID);
        String docUrl = doc.get(DocumentField.URL);
        TermFreqVector vector = luceneSearcher.getIndexReader().getTermFreqVector(documentId, DocumentField.CONTENT);
        GenericDocument gd = new GenericDocument(docId, vector);
        gd.SetDocumentUrl(docUrl);
//        //seteamos el id del documento
//        String docId = doc.get(DocumentField.ID);
//        gd.SetDocumentId(docId);
//
//        //seteamos la url del documento
//        String docUrl = doc.get(DocumentField.URL);
//        gd.SetDocumentUrl(docUrl);



        return gd;
    }

    @Override
    public String[] GetIndexedTerms() throws IOException
    {
        ArrayList<String> result = new ArrayList<String>();
        TermEnum termsIterator = luceneSearcher.getIndexReader().terms();

        while(termsIterator.next())
        {
            Term currentTerm = termsIterator.term();
            result.add(currentTerm.text());
        }

        return result.toArray(new String[result.size()]);
    }

    @Override
    public String[] GetIndexedTerms(int documentId) throws IOException
    {
        TermFreqVector vector = luceneSearcher.getIndexReader().getTermFreqVector(documentId, DocumentField.CONTENT);

        if (vector == null)
            return new String[0];

        return vector.getTerms();
    }

    @Override
    public int GetTermFrecuencyInDocument(int documentId, String term) throws IOException
    {
        if (term == null)
            return 0;

        //buscar en la cache

        TermFreqVector contentVector = luceneSearcher.getIndexReader().getTermFreqVector(documentId, DocumentField.CONTENT);

        if (contentVector == null)
            return 0;

        String[] termsVector = contentVector.getTerms();
        int[] frecuenciesVector = contentVector.getTermFrequencies();

        for(int i = 0; i < termsVector.length; i++)
        {
            if (term.equals(termsVector[i]))
                return frecuenciesVector[i];
        }

        //guardar en la cache

        return 0;
    }
}
