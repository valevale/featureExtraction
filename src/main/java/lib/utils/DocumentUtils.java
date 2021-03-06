package lib.utils;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import segmentation.DocumentCleaner;

public class DocumentUtils {
	/* Prende come input un documento w3c e lo converte in stringa leggibile */
	public static String getStringFromDocument(org.w3c.dom.Document doc)
	{
		try
		{
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		}
		catch(TransformerException ex)
		{
			ex.printStackTrace();
			System.out.println("errore "+ ex);
			return null;
		}
	}
	
	/*pulisce la pagina*/
	public static Document prepareDocument(String html_document, String source) throws Exception {
		String htmlDocumentString = html_document;
		String cleanedHTML = Jsoup.clean(htmlDocumentString, Whitelist.relaxed()
				.addAttributes(":all", "class", "id"));
		Document document = Jsoup.parse(cleanedHTML);
		DocumentCleaner docCleaner = DocumentCleaner.getInstance();
		Document documentCleaned = docCleaner.removeTemplate_server(document, source);
		return documentCleaned;
	}
}
