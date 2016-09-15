package featureExtractor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import org.jsoup.nodes.Document;

import lib.utils.PrettyPrintMap;
import model.PageEntry;
import nlp.NlpFacade;

public class OutputPrinter {

	public static void print(String fileName, List<PageEntry> pages) throws Exception {
		PrintWriter printer = new PrintWriter(fileName, "UTF-8");
		String html;
		for(int i=0; i<pages.size(); i++) {

			html = pages.get(i).getPage().getBody();
			System.out.println("getting entity " +(i+1));

			TextExtractor te = new TextExtractor();

			HashMap<String, List<String>> entities = new HashMap<>();


			String text = te.getTextWithJsoup(html);

			printer.println("JSOUP\n"+text+"\n\n\n");

			entities = NlpFacade.getEntities(text, html);
			//		        System.out.println("***JSOUP***\n"+new PrettyPrintMap<String, String>(entities));
			printer.println("***JSOUP***\n"+new PrettyPrintMap<String, String>(entities)+"\n\n\n");

			text = te.getTextWithBoilerArticle(html);

			printer.println("------------\n\n\n");
			printer.println("BOILER ARTICLE\n"+text+"\n\n\n");

			entities = NlpFacade.getEntities(text, html);
			//		        System.out.println("***BOILER ARTICLE***\n"+new PrettyPrintMap<String, String>(entities));
			printer.println("***BOILER ARTICLE***\n"+new PrettyPrintMap<String, String>(entities)+"\n\n\n");

			text = te.getTextWithBoilerDefault(html);

			printer.println("------------\n\n\n");
			printer.println("BOILER DEFAULT\n"+text+"\n\n\n");

			entities = NlpFacade.getEntities(text, html);
			//		        System.out.println("***BOILER DEFAULT***\n"+new PrettyPrintMap<String, String>(entities));
			printer.println("***BOILER DEFAULT***\n"+new PrettyPrintMap<String, String>(entities)+"\n\n");

			printer.println("________________________________***________________________________\n\n\n\n\n");
		} //end for
		printer.close();
	}
	
//	public static void printText(String fileName, String toPrint, boolean closing) throws BoilerpipeProcessingException, FileNotFoundException, UnsupportedEncodingException {
//		
//		printer.println(toPrint);
//		if (closing)
//			printer.close();
//		else
//			printer.println("\n\n\n");
//	}
	
//	public void createFile(String filename) {
//		PrintWriter printer = new PrintWriter(fileName, "UTF-8");
//	}

	public void printDocument(String fileName, Document doc) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter printer = new PrintWriter(fileName, "UTF-8");
		printer.println(doc.toString());
		printer.close();
	}
	
	public void printDocumentS(String fileName, Document doc) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter printer = new PrintWriter(fileName, "UTF-8");
		printer.println(doc.text());
		printer.close();
	}

}


//while (pages.size()==N_LIMIT) {


//offset+=N_LIMIT;
//pages.clear();
//pages = facade.getPages(N_LIMIT,offset);
//} //end while
//ultima iterazione
//for(int i=0; i<pages.size(); i++) {
//
//html = pages.get(i).getHtml();
//System.out.println("getting entity " +c);
//
//TextExtractor te = new TextExtractor();
//
//HashMap<String, List<String>> entities = new HashMap<>();
//
//
//String text = te.getTextWithJsoup(html);
//
//out.println("JSOUP\n"+text+"\n\n\n");
//
//entities = NlpFacade.getEntities(text, html);
//System.out.println("***JSOUP***\n"+new PrettyPrintMap<String, String>(entities));
//out.println("***JSOUP***\n"+new PrettyPrintMap<String, String>(entities)+"\n\n\n");
//
//text = te.getTextWithBoilerArticle(html);
//
//out.println("------------\n\n\n");
//out.println("BOILER ARTICLE\n"+text+"\n\n\n");
//
//entities = NlpFacade.getEntities(text, html);
//System.out.println("***BOILER ARTICLE***\n"+new PrettyPrintMap<String, String>(entities));
//out.println("***BOILER ARTICLE***\n"+new PrettyPrintMap<String, String>(entities)+"\n\n\n");
//
//text = te.getTextWithBoilerDefault(html);
//
//out.println("------------\n\n\n");
//out.println("BOILER DEFAULT\n"+text+"\n\n\n");
//
//entities = NlpFacade.getEntities(text, html);
//System.out.println("***BOILER DEFAULT***\n"+new PrettyPrintMap<String, String>(entities));
//out.println("***BOILER DEFAULT***\n"+new PrettyPrintMap<String, String>(entities)+"\n\n");
//
//out.println("________________________________***________________________________\n\n\n\n\n");
//c++;
//}