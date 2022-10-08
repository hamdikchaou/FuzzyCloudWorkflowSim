//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fuzzy_param;

public class Fuzzy_function {
    public Fuzzy_function() {
    }

    public static double getRandomNumber(double a, double b) {
        if (b < a) {
            return getRandomNumber(b, a);
        } else {
            double result = a + (1.0 + b - a) * Math.random();
            return !(result > b) && !(result < a) ? convert(result) : getRandomNumber(b, a);
        }
    }

    public static double convert(double a) {
        int result = 10;

        for(int i = 1; (double)i < Fuzzy_parametre.getAfter_dot(); ++i) {
            result *= 10;
        }

        return Math.floor(a * (double)result) / (double)result;
    }

    public static double convert_ceil(double a) {
        int result = 10;

        for(int i = 1; (double)i < Fuzzy_parametre.getAfter_dot(); ++i) {
            result *= 10;
        }

        return Math.ceil(a * (double)result) / (double)result;
    }

    public static double floorconvert(double a) {
        double x = a - Math.floor(a * 100.0) / 100.0;
        double result;
        if (x < 0.005) {
            result = convert(a);
        } else if (x > 0.005) {
            result = convert_ceil(a);
        } else {
            result = convert(a);
        }

        return result;
    }
}
