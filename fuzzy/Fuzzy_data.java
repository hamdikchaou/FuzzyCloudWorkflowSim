//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fuzzy;

import fuzzy_param.Fuzzy_function;
import java.util.LinkedList;
import java.util.List;
import outil.Center;
import outil.Point;

public class Fuzzy_data {
    private List<Point> data_point;
    private List<Center> cluster_point;
    private double fuzziness;
    private int num_clusters;

    public Fuzzy_data(double fuzziness, int num_clusters, int number, double x_min, double x_max, double y_min, double y_max) {
        this.fuzziness = fuzziness;
        this.num_clusters = num_clusters;
        this.data_point = new LinkedList();
        this.cluster_point = new LinkedList();

        int i;
        for(i = 0; i < number; ++i) {
            Point data = new Point(Fuzzy_function.getRandomNumber(x_min, x_max), Fuzzy_function.getRandomNumber(y_min, y_max));
            this.data_point.add(data);
        }

        for(i = 0; i < num_clusters; ++i) {
            Center data = new Center(Fuzzy_function.getRandomNumber(x_min, x_max), Fuzzy_function.getRandomNumber(y_min, y_max));
            this.cluster_point.add(data);
        }

    }

    public List<Point> getData_point() {
        return this.data_point;
    }

    public void setData_point(List<Point> data_point) {
        this.data_point = data_point;
    }

    public Point getData_point(int i) {
        return (Point)this.data_point.get(i);
    }

    public List<Center> getCluster_point() {
        return this.cluster_point;
    }

    public Center getCluuster_point(int i) {
        return (Center)this.cluster_point.get(i);
    }

    public void setCluster_point(List<Center> cluster_point) {
        this.cluster_point = cluster_point;
    }

    public double getFuzziness() {
        return this.fuzziness;
    }

    public void setFuzziness(double fuzziness) {
        this.fuzziness = fuzziness;
    }

    public int getNum_clusters() {
        return this.num_clusters;
    }

    public void setNum_clusters(int num_clusters) {
        this.num_clusters = num_clusters;
    }
}
