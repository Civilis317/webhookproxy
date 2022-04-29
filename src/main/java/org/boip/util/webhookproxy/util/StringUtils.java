package org.boip.util.webhookproxy.util;

public class StringUtils {

    public static boolean isNotEmpty(String value) {
        return value != null && value.trim().length() > 0;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNumerical(String value) {
        if (value == null)
            return false;
        return value.chars().allMatch(Character::isDigit);
    }

}
