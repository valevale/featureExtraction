package lib.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by lorenzoluce on 17/08/16.
 */
public class MapUtils {

    public static void mergeLists(Map<String, List<String>> map, String key, String value) {
        mergeLists(map, key, Arrays.asList(value));
    }

    public static void mergeLists(Map<String, List<String>> map, String key, List<String> value) {
        map.merge(key, new ArrayList<>(value), (a1, a2) -> {a1.addAll(a2); return a1;});
    }

}
