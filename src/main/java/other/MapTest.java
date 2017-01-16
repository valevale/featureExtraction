package other;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

	public static void main(String[] args) {
		Map<Integer,String> map = new HashMap<>();
		map.put(0, "ciao");
		map.put(1, "hey");
		map.put(2, "loll");
		
		int dim = map.size();
		for (int i = 0; i<dim;i++) {
			System.out.println(map.get(i));

			if (i==1) map.remove(1);
		}
		
		System.out.println();
		map = removeGaps(map);
		
		for (int i = 0; i<map.size();i++) {
			System.out.println(map.get(i));
		}
		
	}
	
	public static Map<Integer,String> removeGaps(Map<Integer,String> map) {
		Map<Integer,String> newMap = new HashMap<>();
		int j=0;
		System.out.println("--");
		for (int i=0; i<=map.size();i++) {
			System.out.println(map.get(i));
			if (map.get(i) != null) {
				newMap.put(j, map.get(i));
				j++;
			}
		}
		System.out.println("--");
		return newMap;
	}
}
