import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    static String getAppVersion(String[] strings) {
        if (strings.length < 2)
            return "default";
        return strings[1];
    }

    public static String  getRouteVersion(String[] strings) {
        if (strings.length < 3)
            return "default";
        return strings[2];
    }

    static String getQueueName(String[] strings) {
        if (strings.length < 1)
            return "q1";
        return strings[0];
    }


    public static List<String> getInfraVersionsList(String[] strings) {
        List<String> versions = new ArrayList<>();
        if (strings.length < 1) {
            versions.add("v0");
            return versions;
        } else {
            System.out.println( Arrays.asList(strings));
            return Arrays.asList(strings);
        }
    }
}
