package other;

public class UtilsTest {

	public static void main(String[] args) {
		String ciao = "dklkd";
		String ll = "3";
		System.out.println(prova(ciao));
		System.out.println(ll);
		switch (ll) {
		case "1":  ciao = "k"; break;
		case "2":   ciao = "ks"; break;
		case "3": ciao = "kss"; break;
		default:   ciao = "ksss"; break;
		}
		System.out.println(ciao);

	}

	public static String prova(String ciao) {
		switch (ciao) {
		case "1":  return "1";
		case "2":   return "2";
		case "3": return "cocco";
		case "4":   return "4";
		case "5":  return "6";
		default:   return "389289";
		}
	}

}
