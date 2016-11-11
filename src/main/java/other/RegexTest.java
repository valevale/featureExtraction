package other;

public class RegexTest {

	public static void main(String[] args) {
		String prova = "P.IVA  - LALLI DIEGO LALLI DIEGO 86170 <!-- google_ad_client = \"ca-pub-5542426080701415\"; /* Pagina Azienda */google_ad_slot = \"4305173782\";"
   +" google_ad_width = 336; google_ad_height = 280; //-->: CSO MARCELLI 335 : Isernia : 86170<!--"
    +"google_ad_client = \"ca-pub-5542426080701415\"; /* Pagina Azienda */ google_ad_slot = \"4305173782\";google_ad_width = 336;"
    +"google_ad_height = 280;//-->";
		
		prova = prova.replaceAll("<!--.*-->", "");
		
		System.out.println(prova);
	}
	
}
