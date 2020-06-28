public class Utils {
    static String getAppVersion(String[] strings) {
        if (strings.length < 1)
            return "v0";
        return strings[0];
    }

    static String getQueueName(String[] strings) {
        if (strings.length < 2)
            return "q1";
        return strings[1];
    }
}
