package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

public class CronologiaStampe {

	static PrintWriter testPrinter = null;
	static String path = Main.path;

//	private static void createPrinter() throws IOException {
//		//elimina quello che è stato scritto prima
////		PrintWriter overWriter = new PrintWriter("logger.txt", "UTF-8");
////		overWriter.close();
//		//dev'essere che permette la scrittura consecutiva
//		testPrinter = new PrintWriter(new FileWriter("logger.txt", true));
//	}
	
	
	public static void println(String testo) throws IOException {
		testPrinter = getPrinter();
		testPrinter.println(testo);
//		testPrinter.close();
	}

	private static PrintWriter getPrinter() throws IOException {
		if (testPrinter == null) {
			File dir = new File(path);
			dir.mkdirs();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			testPrinter = new PrintWriter(new FileWriter("logger"+timestamp+".txt", true));
		}
		return testPrinter;
	}

	public static void close() {
		testPrinter.close();
	}

}
