//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fcm;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math3.ml.clustering.Clusterable;

public class DoublePoint implements Clusterable, Serializable {
    private static final long serialVersionUID = 3946024775784901369L;
    private final double[] point;

    public DoublePoint(final double[] point) {
        this.point = point;
    }

    public DoublePoint(final int[] point) {
        this.point = new double[point.length];

        for(int i = 0; i < point.length; ++i) {
            this.point[i] = (double)point[i];
        }

    }

    public double[] getPoint() {
        return this.point;
    }

    public boolean equals(final Object other) {
        return !(other instanceof DoublePoint) ? false : Arrays.equals(this.point, ((DoublePoint)other).point);
    }

    public int hashCode() {
        return Arrays.hashCode(this.point);
    }

    public String toString() {
        return Arrays.toString(this.point);
    }
}
