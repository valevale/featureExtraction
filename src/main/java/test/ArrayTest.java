package test;

import java.util.Arrays;
import java.util.List;


public class ArrayTest {

	public static void main(String[] args) {
		String[] nodesArray = new String[5];
		
		nodesArray[0] = "2";
		nodesArray[1] = "3";
		nodesArray[3] = "aa4";
		
		List<String> ciao = Arrays.asList(nodesArray);
		
		System.out.println(ciao);
		
//		System.out.println(nodesArray[0]);
//		System.out.println(nodesArray[2]);
//		System.out.println(nodesArray[4]);
	}
	
}
