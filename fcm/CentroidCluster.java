//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fcm;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class CentroidCluster<T extends Clusterable> extends Cluster<T> {
    private static final long serialVersionUID = -3075288519071812288L;
    private final Clusterable center;

    public CentroidCluster(final Clusterable center) {
        this.center = center;
    }

    public Clusterable getCenter() {
        return this.center;
    }
}
