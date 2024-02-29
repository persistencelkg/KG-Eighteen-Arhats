package org.lkg.simple;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/2/28 8:23 PM
 */
public class RegxUtil {
    public static boolean isValidPhoneNumber(String phoneNumber) {
        //String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0,1,3,5,6,7,8])|(18[0-9])|166|198|199)\\d{8}$";
        String regex = "^1\\d{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

}
