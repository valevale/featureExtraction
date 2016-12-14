package other;

public class UtilsTest {

	public static void main(String[] args) {
		String ciao = "dklkd";
		System.out.println(prova(ciao));

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
