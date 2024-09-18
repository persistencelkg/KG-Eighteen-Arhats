package org.lkg.enums;

import java.lang.reflect.Array;
import java.util.Arrays;

public interface StringEnum {
    String EMPTY = "";
    String AMPERSAND = "&";
    String AND = "and";
    String AT = "@";
    String STAR = "*";

    String PIPE = "|";

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

    char[] ONE_TO_NINE = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    char[] UPPER_CHAR = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'v', 'W', 'X', 'Y', 'Z'};
    char[] LOWER_CHAR = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    String HTTP_PREFIX = "http://";

    String HTTPS_PREFIX = "https://";

}
