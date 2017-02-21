package other;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lucene.SegmentSearcher;
import main.CronologiaStampe;

public class UtilsTest {

	public static void main(String[] args) throws IOException {

		
		Map<Integer,List<String>> a = new HashMap<>();
		List<String> b = new ArrayList<>();
		b.add("lol");
		a.put(1, b);
		System.out.println(a.get(1));
		b.add("ehm");
		System.out.println(a.get(1));
		
//		CronologiaStampe.println("ciao");
//		CronologiaStampe.close();
//		CronologiaStampe.println("ok");
		
		
//		String currentContent="ci\"\nao";
//		currentContent = currentContent.replaceAll("\"", "");
//		currentContent = currentContent.replaceAll("\n", "");
//		System.out.println(currentContent);
		
//		String path = "ciao/come/va/spero/bene";
//		File dir = new File(path);
//		dir.mkdirs();
//		SegmentSearcher searcher = new SegmentSearcher(path);

		//		Map<Integer,String> a = new HashMap<>();
		//		
		//		a.put(1, "prova1");
		//		a.put(2, "prova2");
		//		a.put(3, "prova3");
		//		a.put(4, "prova4");
		//		a.put(5, "prova5");
		//		
		//		List<Integer> listaSetChiavi1 = new ArrayList<>(a.keySet());
		//		for (int i=0;i<listaSetChiavi1.size();i++) {
		//			for(int j=i+1;j<listaSetChiavi1.size();j++) {
		//				Integer pe = listaSetChiavi1.get(i);
		//				Integer se = listaSetChiavi1.get(j);
		//				System.out.println("vediamo: "+pe+","+se);
		//				System.out.println("cioè: "+a.get(pe)+","+a.get(se));
		//			}
		//		}

		//		String ciao = "dklkd";
		//		String ll = "3";
		//		System.out.println(prova(ciao));
		//		System.out.println(ll);
		//		switch (ll) {
		//		case "1":  ciao = "k"; break;
		//		case "2":   ciao = "ks"; break;
		//		case "3": ciao = "kss"; break;
		//		default:   ciao = "ksss"; break;
		//		}
		//		System.out.println(ciao);
		//		List<String> lista = new ArrayList<>(3);
		//		try {String cosa = lista.get(0);}
		//		catch(Exception e) {System.out.println("errore");}
		//		System.out.println(cosa);
	}

	//	public static String prova(String ciao) {
	//		switch (ciao) {
	//		case "1":  return "1";
	//		case "2":   return "2";
	//		case "3": return "cocco";
	//		case "4":   return "4";
	//		case "5":  return "6";
	//		default:   return "389289";
	//		}
	//	}

}
