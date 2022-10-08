//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package IT2PSO;

import dataPlacement.DataCenter;
import dataPlacement.DataSet;
import dataPlacement.Matrix;
import dataPlacement.Task;
import java.util.ArrayList;
import net.sourceforge.jswarm_pso.FitnessFunction;

public class MyFitnessFunction extends FitnessFunction {
    DataSet dt;
    DataCenter dc;
    ArrayList<DataCenter> datacenter;
    ArrayList<Task> tasks;
    Task currenttask;

    public MyFitnessFunction(ArrayList<Task> TaskList, ArrayList<DataCenter> DCList) {
        this.datacenter = DCList;
        this.tasks = TaskList;
    }

    public double evaluate(double[] position) {
        int dependancy = false;
        int p = 0;
        ++p;
        DataCenter thiscenter = new DataCenter();
        int thisCenterClustering = 0;

        for(int i = 0; i < this.tasks.size(); ++i) {
            long d = Math.round(position[i]);
            String dca = "dc" + d;

            int k;
            for(k = 0; k < this.datacenter.size(); ++k) {
                if (((DataCenter)this.datacenter.get(k)).getName().equals(dca)) {
                    thiscenter = (DataCenter)this.datacenter.get(k);
                }
            }

            for(k = 0; k < ((Task)this.tasks.get(i)).getOutput().size(); ++k) {
                thisCenterClustering += this.calculateClustering((DataSet)((Task)this.tasks.get(i)).getOutput().get(k), thiscenter);
            }
        }

        return (double)thisCenterClustering;
    }

    protected int calculateClustering(DataSet aDataset, DataCenter aDatacenter) {
        int dependancy = 0;

        for(int i = 0; i < aDatacenter.getDatasets().size(); ++i) {
            dependancy += Matrix.calculateDependancyFuzzy(aDataset, (DataSet)aDatacenter.getDatasets().get(i));
        }

        return dependancy;
    }
}
