package main;

import java.util.ArrayList;
import java.util.List;

public class SourceInput {

	static List<String> idSorgenti = new ArrayList<>();
	
	public static void inizializzaLista() {
		idSorgenti.add("5750678b3387e31f516fa1c7");
		idSorgenti.add("5750678b3387e31f516fa1d0");
		idSorgenti.add("5750678b3387e31f516fa1ca");
//		idSorgenti.add("5750678b3387e31f516fa1cd");
//		idSorgenti.add("5750678a3387e31f516fa185");
	}
	
	public static List<String> getSorgenti() {
		return idSorgenti;
	}
	
	
	
}
