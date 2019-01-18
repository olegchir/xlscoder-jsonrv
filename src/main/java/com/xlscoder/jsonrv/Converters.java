package com.xlscoder.jsonrv;

public class Converters {
    public static boolean doubleValueIsInteger(double variable) {
        return (variable == Math.floor(variable)) && !Double.isInfinite(variable);
    }

    /**
     * This is a truncating conversion:
     * double d = 1234.56;
     * long x = (long) d; // x = 1234
     *
     * And this is not:
     * double d = 1234.56;
     * long x = Math.round(d); // x = 1235
     *
     * @param variable
     * @return
     */
    public static long longValue(double variable) {
        return (long) variable;
    }

    public static String universalValue(double variable) {
        if (doubleValueIsInteger(variable)) {
            return Long.toString(longValue(variable));
        } else {
            return Double.toString(variable);
        }
    }
}
