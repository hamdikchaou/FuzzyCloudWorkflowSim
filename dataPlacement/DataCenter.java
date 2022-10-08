//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;

public class DataCenter {
    private String fName;
    private double fSize;
    private double fP_ini;
    private double fP_max;
    private ArrayList<DataSet> fDatasets = new ArrayList();
    private int fTaskExecutionCount = 0;
    private int fDataSetMovementCount = 0;
    private boolean isAtMaxCapacity = false;
    private boolean hasFixedData;

    public DataCenter() {
    }

    public int getTaskExecutionCount() {
        return this.fTaskExecutionCount;
    }

    public int getDatasetMovementCount() {
        return this.fDataSetMovementCount;
    }

    public void resetDataCenter() {
        this.fDatasets.clear();
        this.isAtMaxCapacity = false;
        this.fDataSetMovementCount = 0;
        this.fTaskExecutionCount = 0;
    }

    public void resetDataCenterCounts() {
        this.fDataSetMovementCount = 0;
        this.fTaskExecutionCount = 0;
    }

    public void setMaxCapacity(boolean aValue) {
        this.isAtMaxCapacity = aValue;
    }

    public ArrayList<DataSet> execute(Task aTask) throws Exception {
        if (this.canExecute(aTask)) {
            System.out.println("Datacenter " + this.fName + " is executing " + aTask.getName() + "=" + aTask.getInput() + "...");
        } else {
            ArrayList<DataSet> lRetrieved = this.findRetrievedSets(aTask);
            System.out.print("Datacenter " + this.fName + " is executing " + aTask.getName() + "=" + aTask.getInput() + " by retrieving ");

            for(int i = 0; i < lRetrieved.size(); ++i) {
                System.out.print(lRetrieved.get(i) + "; ");
            }

            System.out.println();
        }

        for(int i = 0; i < aTask.getOutput().size(); ++i) {
            ((DataSet)aTask.getOutput().get(i)).setExists(true);
        }

        ++this.fTaskExecutionCount;
        return aTask.getOutput();
    }

    private ArrayList<DataSet> findRetrievedSets(Task aTask) {
        ArrayList<DataSet> result = new ArrayList();

        for(int i = 0; i < aTask.getInput().size(); ++i) {
            if (!this.fDatasets.contains(aTask.getInput().get(i))) {
                result.add((DataSet)aTask.getInput().get(i));
            }
        }

        return result;
    }

    public boolean canExecute(Task aTask) {
        return this.checkDependancies(aTask);
    }

    private boolean checkDependancies(Task aTask) {
        for(int i = 0; i < aTask.getInput().size(); ++i) {
            if (!this.fDatasets.contains(aTask.getInput().get(i))) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<DataSet> getDatasets() {
        return this.fDatasets;
    }

    public void addDataset(DataSet aDataset) throws Exception {
        if (!this.fDatasets.contains(aDataset)) {
            if (!aDataset.exists()) {
                System.out.println("Non existant dataset " + aDataset + " was added to " + this);
            } else if (this.freeSpace() >= aDataset.getSize()) {
                this.fDatasets.add(aDataset);
                ++this.fDataSetMovementCount;
            } else {
                throw new Exception(this + " cannot add " + aDataset + ", " + aDataset.getSize() + " required, " + this.freeSpace() + " available");
            }
        }
    }

    public double utilisation() {
        double lUtilisation = 0.0;

        for(int i = 0; i < this.fDatasets.size(); ++i) {
            lUtilisation += ((DataSet)this.fDatasets.get(i)).getSize();
        }

        return lUtilisation / this.fSize;
    }

    public double freeSpace() {
        double lUtilisation = 0.0;

        for(int i = 0; i < this.fDatasets.size(); ++i) {
            lUtilisation += ((DataSet)this.fDatasets.get(i)).getSize();
        }

        double availableSize;
        if (this.isAtMaxCapacity) {
            availableSize = this.fSize * this.fP_max;
        } else {
            availableSize = this.fSize * this.fP_ini;
        }

        return availableSize - lUtilisation;
    }

    public void setDatasets(ArrayList<DataSet> aDatasets) {
        this.fDatasets = aDatasets;
    }

    public String getName() {
        return this.fName;
    }

    public void setName(String aName) {
        this.fName = aName;
    }

    public double getP_ini() {
        return this.fP_ini;
    }

    public void setP_ini(double aP_ini) {
        this.fP_ini = aP_ini;
    }

    public double getP_max() {
        return this.fP_max;
    }

    public void setP_max(double aP_max) {
        this.fP_max = aP_max;
    }

    public double getSize() {
        return this.fSize;
    }

    public void setSize(double aSize) {
        this.fSize = aSize;
    }

    public String toString() {
        return this.fName;
    }

    public boolean hasFixedData() {
        return this.hasFixedData;
    }

    public void setHasFixedData(boolean hasFixedData) {
        this.hasFixedData = hasFixedData;
    }
}
