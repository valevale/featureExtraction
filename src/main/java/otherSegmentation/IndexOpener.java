package otherSegmentation;

import java.io.IOException;

import java.io.File;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class IndexOpener {
	public static IndexReader GetIndexReader(String indexDirectoryPath) throws IOException {
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File(indexDirectoryPath)));
        return indexReader;
    }

    /**
     * Returns the total number of documents in the index
     * @return
     * @throws IOException 
     */
    public static Integer TotalDocumentInIndex(String indexDirectoryPath) throws IOException
    {
        Integer maxDoc = GetIndexReader(indexDirectoryPath).maxDoc();
//        System.out.println("MAX DOC "+maxDoc);
        GetIndexReader(indexDirectoryPath).close();
        return maxDoc;
    }
}
