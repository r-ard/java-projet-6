package com.paymybuddy.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class FormatUtils {
    public static int parseIntValue(String value) {
        if(value == null) return 0;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double roundDecimal(double valeur, int precision) {
        BigDecimal bd = BigDecimal.valueOf(valeur);
        bd = bd.setScale(precision, RoundingMode.CEILING);
        return bd.doubleValue();
    }
}
