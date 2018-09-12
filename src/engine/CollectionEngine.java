package engine;

import document.IDocument;
import indexer.*;
import searcher.*;

import java.io.IOException;
import java.util.ArrayList;

public class CollectionEngine implements ICollectionEngine
{
    IIndexer indexer;
    ISearcher searcher;

    public CollectionEngine(ArrayList<String> fileSystemSources, String indexPath) throws Exception
    {
        //seateamos el indexer
        indexer = new Indexer(fileSystemSources, indexPath);

        //construimos el indice
        indexer.BuildIndex();

        //seteamos el searcher
        searcher = new Searcher(indexPath);
    }

    @Override
    public ArrayList<String> GetSupportedExtensions()
    {
        ArrayList<String> clon = new ArrayList<String>();

        for (String ext: indexer.GetSupportedExtensions())
        {
            clon.add(ext);
        }

        return clon;
    }

    @Override
    public void SetSupportedExtensions(ArrayList<String> newExtensions) throws Exception
    {
        indexer.SetSupportedExtensions(newExtensions);
    }

    @Override
    public void BuildIndex()
    {
        try
        {
            indexer.BuildIndex();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int GetDocumentsCount()
    {
        try
        {
            return searcher.GetIndexedDocumentsCount();
        }
        catch(IOException ioe)
        {

            return -1;
        }
    }

    @Override
    public ArrayList<IDocument> GetCollectionDocuments()
    {
        try
        {
            return searcher.GetIndexedDocuments();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public String[] GetCollectionTerms()
    {
        try
        {
            return searcher.GetIndexedTerms();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public String[] GetDocumentTerms(IDocument document)
    {
        return GetDocumentTerms(document.GetDocumentId());
    }

    @Override
    public String[] GetDocumentTerms(String documentId)
    {
        try
        {
            int docId = Integer.parseInt(documentId, 10);
            return searcher.GetIndexedTerms(docId);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public IDocument GetDocumentById(String documentId)
    {
        try
        {
            int docId = Integer.parseInt(documentId, 10);
            return searcher.GetDocument(docId);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public double GetTermFrequencyInDocument(IDocument document, String term)
    {
        return GetTermFrequencyInDocument(document.GetDocumentId(), term);
    }

    @Override
    public double GetTermFrequencyInDocument(String documentId, String term)
    {
        try
        {
            int docId = Integer.parseInt(documentId, 10);
            return searcher.GetTermFrecuencyInDocument(docId, term);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
            return 0.0;
        }
    }
}
