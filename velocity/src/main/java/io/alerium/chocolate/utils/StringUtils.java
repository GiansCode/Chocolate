package io.alerium.chocolate.utils;


import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    private static StringUtils instance;

    public static StringUtils getInstance() {
        if (instance == null) instance = new StringUtils();
        return instance;
    }

    /**
     * Colorize a string.
     *
     * @param str The string you want to colorize
     * @return colorized {@link String}
     */
    public String colorize(String str) {
        char[] b = str.toCharArray();
        for (int i = 0; i < b.length - 1; ++i)
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        return new String(b);
    }

    /**
     * Colorize a list with strings.
     *
     * @param list The list with strings you want to colorize
     * @return colorized {@link List<String>}
     */
    public List<String> colorize(List<String> list) {
        return list.stream().map(this::colorize).collect(Collectors.toList());
    }

    /**
     * Replace things in the string & colorize it.
     *
     * @param str The string you want to replace the placeholders from
     * @return A {@link String} where the placeholders are replaced
     */
    public String replacePlaceholders(String str, String... replacements) {
        if (replacements.length % 2 != 0)
            throw new IllegalArgumentException("All placeholders must have a value.");
        if (str.isEmpty() || replacements.length == 0) return str;
        for (int i = 0; i < replacements.length; i += 2) {
            String key = replacements[i];
            String value = replacements[i + 1];
            if (str.contains(key)) str = str.replace(key, value);
        }
        return colorize(str);
    }

    /**
     * Replace things in the list with strings & colorize it.
     *
     * @param list The list with strings you want to replace the placeholders from
     * @return A {@link List<String>} where the placeholders are replaced
     */
    public List<String> replacePlaceholders(List<String> list, String... replacements) {
        return list.stream().map(s -> replacePlaceholders(s, replacements)).collect(Collectors.toList());
    }
}
