package util;

public class StringUtils {

    public static String multiplyString(String input, int numberOfTimes) {
        if (numberOfTimes < 1) {
            return "";
        }
        return new String(new char[numberOfTimes]).replace("\0", input);
    }

}
