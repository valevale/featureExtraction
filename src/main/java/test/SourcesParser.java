package test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import database.MongoFacade;
import model.Source;

public class SourcesParser {
	public static void main(String[] args) throws IOException {
		MongoFacade facade = new MongoFacade("web_search_pages");
//		String path = "/home/valentina/workspace_nuovo/DataFusion/";
		File webPagesFile = new File("crawler_db_sources.txt");
		String file = IOUtils.toString(new FileReader(webPagesFile));
		String[] sources = file.split("\n\n");
		PrintWriter textPrinter = new PrintWriter("sources.csv", "UTF-8");
		for (int i=0; i< sources.length-1; i++) {

			String s = sources[i];

			String s_id = s.substring(11);
			Pattern pattern = Pattern.compile("\"(.*)");

			Matcher matcher = pattern.matcher(s_id);

			if (matcher.find()) {

				s_id = s_id.replace(matcher.group(1), "");
				s_id = s_id.substring(0, s_id.length()-1);
				String s_count = matcher.group(1).substring(11);
				s_count = s_count.substring(1, s_count.length()-2);


				System.out.println((i+1));
				System.out.print("****Current source_id: ");
				System.out.println(s_id);
				System.out.print("****Current source_count: ");
				System.out.println(s_count);
				Source currentSource = facade.getSourceWithId(s_id);
				String host = currentSource.getHost();
				System.out.println("Host: "+host);
				textPrinter.println(host+","+s_count);
			}	
		}
		//ultimo record
		int last = sources.length-1;
		String s = sources[last];

		String s_id = s.substring(11);
		Pattern pattern = Pattern.compile("\"(.*)");

		Matcher matcher = pattern.matcher(s_id);

		if (matcher.find()) {
			//		    

			s_id = s_id.replace(matcher.group(1), "");
			s_id = s_id.substring(0, s_id.length()-2);
			String s_count = matcher.group(1).substring(11);
			s_count = s_count.substring(1, s_count.length()-2);


			System.out.println((last+1));
			System.out.print("****Current source_id: ");
			System.out.println(s_id);
			System.out.print("****Current source_count: ");
			System.out.println(s_count);
			Source currentSource = facade.getSourceWithId(s_id);
			String host = currentSource.getHost();
			System.out.println("Host: "+host);
			textPrinter.println(host+","+s_count);
		}
		textPrinter.close();
	}
}
