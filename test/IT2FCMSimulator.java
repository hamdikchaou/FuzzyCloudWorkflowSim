//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package test;

import dataPlacement.BEA;
import dataPlacement.BuildTimeAlgorithm;
import dataPlacement.Clusterer;
import dataPlacement.DataCenter;
import dataPlacement.DataSet;
import dataPlacement.IT2FcmPartition;
import dataPlacement.Matrix;
import dataPlacement.RuntimeAlgorithm;
import dataPlacement.Task;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import workflow.Workflow;

public class IT2FCMSimulator extends CloudSimulator {
    private RuntimeAlgorithm fRuntime;
    private BuildTimeAlgorithm fBuildtime;
    private Matrix fClusteredMatrix;
    private Clusterer fClusterer = new BEA();
    public String name = "Flexi";
    private LinkedList<Integer> fDataRetrieved;
    private LinkedList<Integer> fDataSent;
    private LinkedList<Integer> fDataRescheduled;
    private LinkedList<Integer> fDataReschedules;
    private LinkedList<Double> fDataRetrievedSize;
    private LinkedList<Double> fDataSentSize;
    private LinkedList<Double> fDataRescheduledSize;
    private LinkedList<Double> fMovementAverage;
    private LinkedList<Double> fMovementStandardDeviation;
    private LinkedList<Double> fTaskExecutionAverage;
    private LinkedList<Double> fTaskExecutionStandardDeviation;

    public IT2FCMSimulator(ArrayList<DataCenter> aDataCenters, RuntimeAlgorithm aRuntime, BuildTimeAlgorithm aBuildtime) {
        super(aDataCenters);
        this.fRuntime = aRuntime;
        this.fBuildtime = aBuildtime;
        this.fDataRetrieved = new LinkedList();
        this.fDataSent = new LinkedList();
        this.fDataRescheduled = new LinkedList();
        this.fDataRetrievedSize = new LinkedList();
        this.fDataSentSize = new LinkedList();
        this.fDataRescheduledSize = new LinkedList();
        this.fDataReschedules = new LinkedList();
        this.fMovementAverage = new LinkedList();
        this.fMovementStandardDeviation = new LinkedList();
        this.fTaskExecutionAverage = new LinkedList();
        this.fTaskExecutionStandardDeviation = new LinkedList();
    }

    public String simulateWorkflow(Workflow aWorkflow) throws Exception {
        String result = "";
        ArrayList<DataCenter> lUtilisedCenters = this.fBuildtime.distribute(aWorkflow);
        System.out.println("Build time distribution completed_from_FlexiSimulator Class");
        System.out.println("--Utilised DataCenters ");
        Iterator var4 = lUtilisedCenters.iterator();

        DataCenter thisCenter;
        while(var4.hasNext()) {
            thisCenter = (DataCenter)var4.next();
            System.out.print(thisCenter.getName() + "= {");
            Iterator var6 = thisCenter.getDatasets().iterator();

            while(var6.hasNext()) {
                DataSet ds = (DataSet)var6.next();
                System.out.print(ds.getName() + " | ");
            }

            System.out.print("}");
            System.out.println();
        }

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
            thisCenter = (DataCenter)lUtilisedCenters.get(i);
            result = result + thisCenter.getName() + " : ";

            for(int j = 0; j < thisCenter.getDatasets().size(); ++j) {
                result = result + ((DataSet)thisCenter.getDatasets().get(j)).getName() + " (" + ((DataSet)thisCenter.getDatasets().get(j)).getSize() + "); ";
            }

            result = result + "\r\n";
            thisCenter.resetDataCenterCounts();
        }

        result = result + "\r\n";
        Matrix fDependancyMatrix = new Matrix(aWorkflow);
        System.out.println(fDependancyMatrix.toString());
        this.fClusteredMatrix = this.fClusterer.cluster(fDependancyMatrix);
        System.out.println(this.fClusteredMatrix.toString());
        IT2FcmPartition ff = new IT2FcmPartition(lUtilisedCenters, this.fClusteredMatrix);
        lUtilisedCenters = ff.getFuzzyDC();
        System.out.println("fuzzy flexi-----------------------------" + lUtilisedCenters.toString());
        this.increaseDatacenterCapacity();
        System.out.println("-->Setting used DataCenters at maximum capacity = " + ((DataCenter)lUtilisedCenters.get(0)).getSize());
        System.out.println("\n-->Going From BuildTime to the runtime stage (executing the workflow tasks by order)");
        this.fRuntime.run(lUtilisedCenters, aWorkflow);
        result = result + this.fRuntime.getReport();
        this.fDataReschedules.add(this.fRuntime.getTotalDataReschedules());
        this.fDataRescheduled.add(this.fRuntime.getTotalDataRescheduled());
        this.fDataRescheduledSize.add(this.fRuntime.getTotalDataRescheduledSize());
        this.fDataRetrieved.add(this.fRuntime.getTotalDataRetrieved());
        this.fDataRetrievedSize.add(this.fRuntime.getTotalDataRetrievedSize());
        this.fDataSent.add(this.fRuntime.getTotalDataSent());
        this.fDataSentSize.add(this.fRuntime.getTotalDataSentSize());
        this.calculateAverageAndSD();
        return result;
    }

    private void calculateAverageAndSD() {
        double lAverageDataMovement = 0.0;
        double lAverageTaskExecution = 0.0;
        double lDataMovementSD = 0.0;
        double lTaskExecutionSD = 0.0;

        for(int i = 0; i < this.fDatacenters.size(); ++i) {
            lAverageDataMovement += (double)((DataCenter)this.fDatacenters.get(i)).getDatasetMovementCount();
            lDataMovementSD += Math.pow((double)((DataCenter)this.fDatacenters.get(i)).getDatasetMovementCount(), 2.0);
            lAverageTaskExecution += (double)((DataCenter)this.fDatacenters.get(i)).getTaskExecutionCount();
            lTaskExecutionSD += Math.pow((double)((DataCenter)this.fDatacenters.get(i)).getTaskExecutionCount(), 2.0);
        }

        lAverageDataMovement /= (double)this.fDatacenters.size();
        lAverageTaskExecution /= (double)this.fDatacenters.size();
        lDataMovementSD /= (double)this.fDatacenters.size();
        lTaskExecutionSD /= (double)this.fDatacenters.size();
        lDataMovementSD -= Math.pow(lAverageDataMovement, 2.0);
        lTaskExecutionSD -= Math.pow(lAverageTaskExecution, 2.0);
        lDataMovementSD = Math.sqrt(lDataMovementSD);
        lTaskExecutionSD = Math.sqrt(lTaskExecutionSD);
        this.fMovementAverage.add(lAverageDataMovement);
        this.fTaskExecutionAverage.add(lAverageTaskExecution);
        this.fMovementStandardDeviation.add(lDataMovementSD);
        this.fTaskExecutionStandardDeviation.add(lTaskExecutionSD);
    }

    public String toString() {
        return this.name;
    }

    public double getAverageDataRescheduled() {
        return this.average(this.fDataRescheduled);
    }

    public double getAverageDataReschedules() {
        return this.average(this.fDataReschedules);
    }

    public double getAverageDataRetrieved() {
        return this.average(this.fDataRetrieved);
    }

    public double getAverageDataSent() {
        return this.average(this.fDataSent);
    }

    public double getAverageDataRescheduledSize() {
        return this.average(this.fDataRescheduledSize);
    }

    public double getAverageDataRetrievedSize() {
        return this.average(this.fDataRetrievedSize);
    }

    public double getAverageDataSentSize() {
        return this.average(this.fDataSentSize);
    }

    public double getMovementAverage() {
        return this.average(this.fMovementAverage);
    }

    public double getMovementSD() {
        return this.average(this.fMovementStandardDeviation);
    }

    public double getTaskExecutionAverage() {
        return this.average(this.fTaskExecutionAverage);
    }

    public double getTaskExecutionSD() {
        return this.average(this.fTaskExecutionStandardDeviation);
    }
}
