//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.List;

public class FuzzyCenter extends FuzzyPoint {
    private List<FuzzyPoint> data_point;

    public FuzzyCenter(double dep, String name, double size) {
        super(dep, name, size);
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String toString3() {
        String result = "DataCenter: " + this.name + " dependency_center: " + this.dep + " cumulative_size: " + this.size;
        return result;
    }
}
