//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

public class FuzzyPoint {
    protected double dep;
    protected String name;
    protected double size;

    public FuzzyPoint(double dep, String name, double size) {
        this.dep = dep;
        this.name = name;
        this.size = size;
    }

    public String toString3() {
        String result = "dataset: " + this.name + " dependency: " + this.dep + " with size: " + this.size;
        return result;
    }
}
