import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    static String getAppVersion(String[] strings) {
        if (strings.length < 1)
            return "v0";
        return strings[0];
    }

    public static String getRouteVersion(String[] strings, String appVersion) {
        if (strings.length < 2)
            return appVersion;
        return strings[1];
    }

    static String getQueueName(String[] strings) {
        if (strings.length < 2)
            return "q1";
        return strings[1];
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
