//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package test;

import dataPlacement.BuildTime;
import dataPlacement.DataCenter;
import dataPlacement.DataSet;
import dataPlacement.Runtime;
import dataPlacement.RuntimeAlgorithm;
import dataPlacement.Task;
import java.util.ArrayList;
import workflow.Workflow;

public class SmartSimulator extends CloudSimulator {
    public SmartSimulator(ArrayList<DataCenter> aDatacenters) {
        super(aDatacenters);
    }

    public String simulateWorkflow(Workflow aWorkflow) {
        String result = "";
        BuildTime lBuilder = new BuildTime(this.fDatacenters);

        try {
            ArrayList<DataCenter> lUtilisedCenters = lBuilder.distribute(aWorkflow);

            int i;
            for(i = 0; i < aWorkflow.getTasks().size(); ++i) {
                result = result + aWorkflow.getTasks().get(i) + ": ";

                for(int j = 0; j < ((Task)aWorkflow.getTasks().get(i)).getInput().size(); ++j) {
                    result = result + ((Task)aWorkflow.getTasks().get(i)).getInput().get(j) + "; ";
                }

                result = result + "\r\n";
            }

            result = result + "\r\n";

            for(i = 0; i < lUtilisedCenters.size(); ++i) {
                DataCenter thisCenter = (DataCenter)lUtilisedCenters.get(i);
                result = result + thisCenter.getName() + " : ";

                for(int j = 0; j < thisCenter.getDatasets().size(); ++j) {
                    result = result + ((DataSet)thisCenter.getDatasets().get(j)).getName() + " (" + ((DataSet)thisCenter.getDatasets().get(j)).getSize() + "); ";
                }

                result = result + "\r\n";
            }

            result = result + "\r\n";
            this.increaseDatacenterCapacity();
            RuntimeAlgorithm lRunner = new Runtime(lBuilder);
            lRunner.run(this.fDatacenters, aWorkflow);
            result = result + lRunner.getReport();
        } catch (Exception var8) {
            System.out.println("ERROR: " + var8.getMessage());
            System.exit(-1);
        }

        System.out.println("hounaaaaaa" + result);
        return result;
    }

    public String toString() {
        return "BuildAndRun";
    }
}
