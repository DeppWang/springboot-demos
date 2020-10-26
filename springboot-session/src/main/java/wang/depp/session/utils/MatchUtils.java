package wang.depp.session.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MatchUtils {
    public MatchUtils() {
    }

    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str) || "null".equalsIgnoreCase(str) || str.trim().length() == 0;
    }

    public static boolean isEmpty(Arrays[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(List<?> lis) {
        return lis == null || lis.size() == 0;
    }

    public static boolean isEmpty(Iterator<?> ita) {
        return ita == null || !ita.hasNext();
    }

    public static boolean isEmpty(StringBuffer sbf) {
        return sbf == null || sbf.length() == 0;
    }
}
