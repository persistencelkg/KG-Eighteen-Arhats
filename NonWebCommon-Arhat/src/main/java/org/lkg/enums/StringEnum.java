package org.lkg.enums;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public interface StringEnum {
    String EMPTY = "";
    String AMPERSAND = "&";
    String AND = "and";
    String AT = "@";
    String STAR = "*";

    String SLASH = "/";
    String HASH = "#";

    String PIPE = "|";
    String QUESTION = "?";
    String PLUS = "+";

    String BACK_SLASH = "\\";
    String COLON = ":";
    String COMMA = ",";
    String DASH = "-";
    String DOLLAR = "$";
    String DOT = ".";

    String DOTDOT = "..";

    String SPACE = " ";

    String UNDERSCORE = "_";
    String SEMICOLON = ";";


    String LEFT_SQ_BRACKET = "[";
    String RIGHT_SQ_BRACKET = "]";
    String LEFT_BRACE = "{";
    String RIGHT_BRACE = "}";
    String LEFT_CHEV = "<";
    String RIGHT_CHEV = ">";
    String LEFT_BRACKET = "(";
    String RIGHT_BRACKET = ")";
    String DOLLAR_LEFT_BRACE = "${";
    String HASH_LEFT_BRACE = "#{";

    char[] ONE_TO_NINE = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    char[] UPPER_CHAR = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'v', 'W', 'X', 'Y', 'Z'};
    char[] LOWER_CHAR = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    String HTTP_PREFIX = "http://";

    String HTTPS_PREFIX = "https://";


    static String getEscape(String ch) {
        return BACK_SLASH + ch;
    }

    static String getEscapeDot() {
        return getEscape(DOT);
    }

    static String getEscapeComma() {
        return getEscape(COMMA);
    }


    Map<String, String> wellKnownSimplePrefixes = new HashMap<String, String>(8) {{
        put(LEFT_BRACE, RIGHT_BRACE);
        put(LEFT_SQ_BRACKET, RIGHT_SQ_BRACKET);
        put(LEFT_BRACKET, RIGHT_BRACKET);
        put(LEFT_CHEV, RIGHT_CHEV);
        put(HASH_LEFT_BRACE, RIGHT_BRACE);
        put(DOLLAR_LEFT_BRACE, RIGHT_BRACE);

    }};

    static void addPrefixPartner(String prefix, String suffix) {
        wellKnownSimplePrefixes.put(prefix, suffix);
    }
}
