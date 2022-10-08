//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fcm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.ml.clustering.Clusterable;

public class Cluster<T extends Clusterable> implements Serializable {
    private static final long serialVersionUID = -3442297081515880464L;
    private final List<T> points = new ArrayList();

    public Cluster() {
    }

    public void addPoint(final T point) {
        this.points.add(point);
    }

    public List<T> getPoints() {
        return this.points;
    }
}
