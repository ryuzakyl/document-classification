package indexer;

import document.DocumentField;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Indexer implements IIndexer
{
    //directorios de los documentos
    private ArrayList<String> fileSystemSources;

    //path del indice
    private String indexPath;

    //filtro de los archivos a indexar
    private ArrayList<String> extensionsFilter;

    //encargado de crear el indice o abrir uno
    private IndexWriter luceneIndex;

    public Indexer(ArrayList<String> fileSystemSources, String indexPath) throws Exception
    {
        if (!ValidIndexPath(indexPath))
            throw new Exception("Invalid index path.");

        if (fileSystemSources == null)
            throw new Exception("Invalid document sources.");

        for(String src: fileSystemSources)
        {
            if (!ValidIndexPath(src))
            {
                throw new Exception("Invalid source path.");
            }

        }

        //seteamos las fuentes de los documentos
        this.fileSystemSources = new ArrayList<String>(fileSystemSources);

        //seteamos el filtro por defecto
        SetFilter();

        //seteamos el indexer
        this.indexPath = indexPath;
        SetIndexWriter(indexPath);
    }

    private boolean ValidIndexPath(String indexPath)
    {
        File indexDirectory = new File(indexPath);
        return indexDirectory.exists() && indexDirectory.isDirectory();
    }

    private void SetFilter()
    {
        this.extensionsFilter = new ArrayList<String>();

        //agregamos los filtros
        this.extensionsFilter.add(".txt");
    }

    private void SetIndexWriter(String indexPath) throws IOException
    {
        Directory indexDirectory = FSDirectory.open(new File(indexPath));
        luceneIndex = new IndexWriter(indexDirectory, new StandardAnalyzer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED);
        luceneIndex.setUseCompoundFile(false);
    }

    @Override
    public void BuildIndex() throws Exception
    {
        //indexamos cada uno de los directorios
        for(String dir: fileSystemSources)
        {
            IndexDirectory(dir);
        }

        luceneIndex.optimize();
        luceneIndex.close();
    }

    private void IndexDirectory(String directoryPath)  throws Exception
    {
        File dir = new File(directoryPath);

        File[] children = dir.listFiles();

        if (children == null)
            return;

        for(File item: children)
        {
            if (item.isDirectory())
                IndexDirectory(item.getAbsolutePath());
            else
                IndexFile(item);
        }
    }

    private void IndexFile(File file) throws Exception
    {
        if (file.exists() && file.canRead() && PassesFilter(file.getName()))
        {
            /*
            *   Mandamos a indexar el documento. En el futuro me gustaria usar
            *   Tika, pues nos permite indexar .pdf, .docx, .odt, etc.
            */

            Document doc = new Document();

            //agregamos el id del documento
            int id = luceneIndex.maxDoc();
            doc.add(new Field(DocumentField.ID, id + "", Field.Store.YES, Field.Index.NO));
            //agregamos la direccion del recurso
            doc.add(new Field(DocumentField.URL, file.getAbsolutePath(), Field.Store.YES, Field.Index.NO));
            //le agregamos el contenido
            doc.add(new Field(DocumentField.CONTENT, new FileReader(file), Field.TermVector.YES));

            //agregamos el documento al indice
            luceneIndex.addDocument(doc);
        }
    }

    private boolean PassesFilter(String fileName)
    {
        for(String ext: extensionsFilter)
        {
            if (fileName.endsWith(ext))
                return true;
        }

        return false;
    }

    @Override
    public void SetIndexPath(String indexPath) throws Exception
    {
       this.indexPath = indexPath;
       SetIndexWriter(indexPath);
    }

    @Override
    public String GetIndexPath()
    {
        return this.indexPath;
    }

    @Override
    public ArrayList<String> GetSupportedExtensions()
    {
        return this.extensionsFilter;
    }

    @Override
    public void SetSupportedExtensions(ArrayList<String> newExtensions) throws Exception
    {
        this.extensionsFilter = new ArrayList<String>(newExtensions);
    }
}
