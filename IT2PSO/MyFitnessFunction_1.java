//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package IT2PSO;

import dataPlacement.DataCenter;
import dataPlacement.DataSet;
import dataPlacement.Task;
import java.util.ArrayList;
import net.sourceforge.jswarm_pso.FitnessFunction;

public class MyFitnessFunction_1 extends FitnessFunction {
    DataSet dt;
    DataCenter dc;

    public MyFitnessFunction_1(DataSet dataset, DataCenter datacenter) {
        this.dt = dataset;
        this.dc = datacenter;
    }

    public double evaluate(double[] position) {
        int dependancy = 0;
        int p = false;

        for(int i = 0; i < this.dc.getDatasets().size(); ++i) {
            int lCount = 0;
            ArrayList<Task> ds1Tasks = this.dt.getTasks();
            ArrayList<Task> ds2Tasks = ((DataSet)this.dc.getDatasets().get(i)).getTasks();

            for(int j = 0; j < ds1Tasks.size(); ++j) {
                if (ds2Tasks.contains(ds1Tasks.get(j))) {
                    ++lCount;
                }
            }

            dependancy += lCount;
            position[i] += (double)lCount;
        }

        return (double)dependancy;
    }
}
