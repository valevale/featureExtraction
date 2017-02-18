//package main;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//public class CronologiaStampe {
//	
//	static PrintWriter testPrinter;
//	
//	public static void createPrinter() throws IOException {
//		//elimina quello che è stato scritto prima
//		PrintWriter overWriter = new PrintWriter("logger.txt", "UTF-8");
//		overWriter.close();
//		//dev'essere che permette la scrittura consecutiva
//		PrintWriter testPrinter = new PrintWriter(new FileWriter("logger.txt", true));
//	}
//
//	public static PrintWriter getPrinter() {
//		return testPrinter;
//	}
//	
//	public static void close() {
//		testPrinter.close();
//	}
//	
//}
