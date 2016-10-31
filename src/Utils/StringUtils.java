package Utils;

import java.util.Arrays;

/**
 *
 * @author Reuel
 */
public class StringUtils {

    public static String quoteValues(String input, String separator) {
        String[] split = ("" + separator + input).split(separator);
        String output = Arrays.asList(split).stream().reduce((left, right) -> {
            return left + "\"" + right.trim() + "\",";
        }).get();

        return output.substring(0, output.length() - 1);
    }

    public static void main(String[] args) {

        String input = "reuel,test, quote_";
        System.out.println(StringUtils.quoteValues(input, ","));
    }
}
