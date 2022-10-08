//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import realworkflow.Log;

public class DataSet {
    private String fName;
    private double fSize;
    private ArrayList<Task> fTasks = new ArrayList();
    private DataCenter fDC;
    private DataCenter fixedAddress;
    private boolean fExists;
    private boolean fWasGenerated = false;

    public DataSet() {
    }

    public void setExists(boolean aExists) {
        this.fExists = aExists;
    }

    public boolean exists() {
        return this.fExists;
    }

    public DataCenter getDC() {
        return this.fDC;
    }

    public void setDC(DataCenter aDC) {
        this.fDC = aDC;
    }

    public String getName() {
        return this.fName;
    }

    public void setName(String aName) {
        this.fName = aName;
    }

    public double getSize() {
        return this.fSize;
    }

    public void setSize(double aSize) {
        this.fSize = aSize;
    }

    public ArrayList<Task> getTasks() {
        return this.fTasks;
    }

    public void addTask(Task aTask) {
        if (!this.fTasks.contains(aTask)) {
            this.fTasks.add(aTask);
        }

    }

    public void setTasks(ArrayList<Task> aTasks) {
        this.fTasks = aTasks;
    }

    public String toString() {
        return this.fName;
    }

    public void afficheDataset(DataSet fdata) {
        Log.print("Dataset:" + fdata.getName());
        Log.print(" Size:" + fdata.getSize());
        Log.print(" Generated:" + fdata.wasGenerated());
        Log.printLine();
    }

    public void setWasGenerated(boolean aWasGenerated) {
        this.fWasGenerated = aWasGenerated;
    }

    public boolean wasGenerated() {
        return this.fWasGenerated;
    }

    public DataCenter getFixedAddress() {
        return this.fixedAddress;
    }

    public void setFixedAddress(DataCenter fixedAddress) {
        this.fixedAddress = fixedAddress;
    }
}
