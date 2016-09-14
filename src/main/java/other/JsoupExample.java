package other;

import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupExample {

	public static void main(String[] args) throws IOException {

		Document doc = Jsoup.connect("http://www.repubblica.it/politica/2016/09/02/news/raggi_a_lavoro_per_cercare_nuovi_nomi_sulle_chat_l_allarme_degli_attivisti_se_forza_con_marra_viene_giu_il_mondo_-147049578/?ref=HREA-1").get();


		String text = doc.body().text(); // "An example link"
		
		System.out.println(text);
		
		PrintWriter out = new PrintWriter("output.txt", "UTF-8");
	    out.println(text);
	    out.close();
		
		
		
	}
}
