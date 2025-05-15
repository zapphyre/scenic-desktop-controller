package org.remote.desktop.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumUtil {

    public static double mapVal(double value, int start1, int stop1, int start2, int stop2) {
//        System.out.println("mapVal: " + value);
        double v = start2 + (stop2 - start2) * (value - start1) / (stop1 - start1);

//        System.out.println("to: " + v);
//        System.out.println();
        return v;
    }


    public static double mapClamped(double value, double inMin, double inMax, double outMin, double outMax) {
        if (inMin == inMax) {
           return outMin;
        }

        System.out.println("value: " + value);
        System.out.println("inMin: " + inMin);
        System.out.println("inMax: " + inMax);
        System.out.println("outMin: " + outMin);
        System.out.println("outMax: " + outMax);

        // Clamp input to [inMin, inMax]
        double clamped = Math.max(inMin, Math.min(value, inMax));

        // Linear interpolation within range
        double mapped = outMin + (clamped - inMin) * (outMax - outMin) / (inMax - inMin);

        System.out.println("result = " + mapped);
        System.out.println();
        return mapped;
    }
}
