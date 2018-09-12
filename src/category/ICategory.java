package category;

import document.IDocument;

public interface ICategory
{
    //obtiene los documentos pertenecientes a esta clase
    IDocument[] GetDocuments();
    double GetFrequencyPerTerm(String term);
    double GetTotalFrequency();
    double GetDocumentCount();
    double GetDocumentCountPerTerm(String term);
    String GetName();
}
