package indexer;

import java.io.IOException;
import java.util.ArrayList;

public interface IIndexer
{
    void BuildIndex() throws Exception;

    void SetIndexPath(String indexPath) throws Exception;
    String GetIndexPath();

    //obtiene la lista de extensiones de archivos soportadas
    ArrayList<String> GetSupportedExtensions();

    //setea la lista de extensiones de archivos soportadas
    void SetSupportedExtensions(ArrayList<String> newExtensions) throws Exception;

    //ToDo: Si acaso agregar metodos de AddDocument, DeleteDocument, etc. para actualizar el index
}
