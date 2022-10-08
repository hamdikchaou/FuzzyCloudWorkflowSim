//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import workflow.Workflow;

public class Matrix {
    private ArrayList<DataSet> fDatasets;
    private int[][] fData;

    public Matrix() {
        this.fDatasets = new ArrayList();
        this.fData = new int[0][0];
    }

    public Matrix(Workflow aWorkflow) {
        this();

        for(int i = 0; i < aWorkflow.getDatasets().size(); ++i) {
            if (((DataSet)aWorkflow.getDatasets().get(i)).exists()) {
                this.addDataset((DataSet)aWorkflow.getDatasets().get(i));
            }
        }

    }

    public int[][] getData() {
        return this.fData;
    }

    public double[][] getDataDouble(int[][] data) {
        double[][] mat = new double[0][0];

        for(int i = 0; i < data.length; ++i) {
            for(int j = 0; j < data.length; ++j) {
                mat[i][j] = (double)data[i][j];
            }
        }

        return mat;
    }

    public ArrayList<DataSet> getDatasets() {
        return this.fDatasets;
    }

    public void addDataset(DataSet aDataset) {
        if (!this.fDatasets.contains(aDataset)) {
            this.addRowAndColumn();

            for(int i = 0; i < this.fData.length - 1; ++i) {
                int lDependancy = calculateDependancy(aDataset, (DataSet)this.fDatasets.get(i));
                this.fData[i][this.fData.length - 1] = lDependancy;
                this.fData[this.fData.length - 1][i] = lDependancy;
            }

            this.fData[this.fData.length - 1][this.fData.length - 1] = aDataset.getTasks().size();
            this.fDatasets.add(aDataset);
        }
    }

    private void addRowAndColumn() {
        int lNewSize = this.fData.length + 1;
        int[][] lNewMatrix = new int[lNewSize][lNewSize];

        for(int i = 0; i < lNewSize - 1; ++i) {
            for(int j = 0; j < lNewSize - 1; ++j) {
                lNewMatrix[i][j] = this.fData[i][j];
            }
        }

        this.fData = lNewMatrix;
    }

    public static int calculateDependancy(DataSet aDataset1, DataSet aDataset2) {
        int lCount = 0;
        ArrayList<Task> ds1Tasks = aDataset1.getTasks();
        ArrayList<Task> ds2Tasks = aDataset2.getTasks();

        for(int i = 0; i < ds1Tasks.size(); ++i) {
            if (ds2Tasks.contains(ds1Tasks.get(i))) {
                ++lCount;
            }
        }

        return lCount;
    }

    public static int calculateDependancyFuzzy(DataSet aDataset1, DataSet aDataset2) {
        int lCount = 0;
        ArrayList<Task> ds1Tasks = aDataset1.getTasks();
        ArrayList<Task> ds2Tasks = aDataset2.getTasks();

        for(int i = 0; i < ds1Tasks.size(); ++i) {
            if (ds2Tasks.contains(ds1Tasks.get(i))) {
                ++lCount;
            }
        }

        return lCount;
    }

    public String toString() {
        String result = "";

        for(int i = 0; i < this.fData.length; ++i) {
            for(int j = 0; j < this.fData[i].length; ++j) {
                result = result + this.fData[i][j] + " ";
            }

            result = result + "\r\n";
        }

        return result;
    }

    public String toString2(Matrix a, int start, int end) {
        String result = "";

        for(int i = start; i < end; ++i) {
            for(int j = 0; j < end; ++j) {
                result = result + this.fData[i][j] + " ";
            }

            result = result + "\r\n";
        }

        return result;
    }

    public int dep(Matrix a, int start, int end) {
        return a.fData[start][end];
    }
}
