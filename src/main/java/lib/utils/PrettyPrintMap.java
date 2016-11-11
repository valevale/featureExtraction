package lib.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scala.Tuple2;

public class PrettyPrintMap<K, V> {
    private Map<String, List<String>> mapList;

    public PrettyPrintMap(HashMap<String, List<String>> entities) {
        this.mapList = entities;
    }

    



	public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<String, List<String>>> iter = mapList.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, List<String>> entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append(entry.getValue().size());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ').append('\n').append('\n');
            }
        }
        return sb.toString();

    }
}